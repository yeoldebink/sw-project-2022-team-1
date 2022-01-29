package il.cshaifa.hmo_system.on_site_client;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.client_base.HMOClient;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.messages.OnSiteEntryMessage;
import il.cshaifa.hmo_system.messages.OnSiteLoginMessage;
import il.cshaifa.hmo_system.messages.OnSiteQueueMessage;
import il.cshaifa.hmo_system.messages.UpdateAppointmentMessage;
import il.cshaifa.hmo_system.on_site_client.events.CloseStationEvent;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteEntryEvent;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteLoginEvent;
import il.cshaifa.hmo_system.on_site_client.events.PatientWalkInAppointmentEvent;
import il.cshaifa.hmo_system.on_site_client.events.StaffNextAppointmentEvent;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.greenrobot.eventbus.EventBus;

public class HMOOnSiteClient extends HMOClient {

  private static HMOOnSiteClient client = null;

  private HMOOnSiteClient(String host, int port) {
    super(host, port);
  }

  public static HMOOnSiteClient getClient() {
    if (client == null) {
      client = new HMOOnSiteClient("localhost", 3000);
    }
    return client;
  }

  //
  // ************************* METHODS TO CALL FROM GUI *************************
  //

  /**
   * @param user The id of the login request
   * @param password The password the user has entered
   * @throws java.io.IOException SQL exception
   */
  public void loginRequest(int user, String password, Clinic clinic, OnSiteLoginAction action) throws IOException {
    sendToServer(new OnSiteLoginMessage(user, password, clinic, action));
  }

  public void patientEntryRequest(int id) throws IOException {
    sendToServer(new OnSiteEntryMessage(id));
  }

  public Clinic getStationClinic() {
    return connected_employee_clinics.get(0);
  }

  public void patientWalkInRequest(Patient patient, AppointmentType appt_type) throws IOException {
    sendToServer(OnSiteQueueMessage.pushMessage(patient, appt_type));
  }

  public void staffQueuePopRequest() throws IOException {
    sendToServer(OnSiteQueueMessage.popMessage());
  }

  public void updateAppointmentComments(Appointment appointment) throws IOException {
    sendToServer(new UpdateAppointmentMessage(appointment));
  }

  //
  // ********************************* HANDLERS *********************************
  //

  @Override
  protected void handleMessageFromServer(Object message) {
    if (message instanceof OnSiteEntryMessage) {
      handleOnSiteEntryMessage((OnSiteEntryMessage) message);
    } else if (message instanceof OnSiteQueueMessage) {
      handleOnSiteQueueMessage((OnSiteQueueMessage) message);
    } else if (message instanceof OnSiteLoginMessage) {
      handleOnSiteLoginMessage((OnSiteLoginMessage) message);
    } else {
      super.handleMessageFromServer(message);
    }
  }

  private void handleOnSiteEntryMessage(OnSiteEntryMessage message) {
    EventBus.getDefault().post(OnSiteEntryEvent.entryResponseEvent(message.q_appt, message.patient, this));
  }

  private void handleOnSiteQueueMessage(OnSiteQueueMessage message) {
    switch (message.action) {
      case PUSH:
        if (message.rejection_reason != null) {
          EventBus.getDefault().post(PatientWalkInAppointmentEvent.newWalkInRejection(message.rejection_reason, this));
        } else {
          EventBus.getDefault().post(PatientWalkInAppointmentEvent.newWalkInResponse(message.q_appt, this));
        }
        break;

      case POP:
      case UPDATE_QUEUE:
        EventBus.getDefault().post(StaffNextAppointmentEvent.nextAppointmentResponseEvent(message.updated_queue,
            message.queue_timestamp, message.q_appt, this
        ));
        break;

      default:
        new NotImplementedException("QueueMessage action type not implemented").printStackTrace();
        break;
    }
  }

  private void handleOnSiteLoginMessage(OnSiteLoginMessage message) {
    if (message.action != OnSiteLoginAction.LOGIN && message.user != null) {

      try {
        this.closeConnection();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }

      Platform.runLater(() -> {
        var dialog = new Dialog<String>();
        dialog.setContentText(String.format("%s closed by manager", message.action == OnSiteLoginAction.CLOSE_CLINIC ? "Clinic" : "Station"));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
        System.exit(0);
      });
    }

    OnSiteLoginEvent event = new OnSiteLoginEvent(0, null, message.clinic, this);
    event.staff_member_queue = message.staff_member_queue;
    event.queue_timestamp = message.queue_timestamp;
    super.handleLoginMessage(message, event);
  }
}
