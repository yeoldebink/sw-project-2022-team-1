package il.cshaifa.hmo_system.on_site_client;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.client_base.HMOClient;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.messages.OnSiteEntryMessage;
import il.cshaifa.hmo_system.messages.OnSiteLoginMessage;
import il.cshaifa.hmo_system.messages.OnSiteQueueMessage;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteEntryEvent;
import il.cshaifa.hmo_system.on_site_client.events.PatientWalkInAppointmentEvent;
import java.io.IOException;
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
  public void loginRequest(int user, String password, Clinic clinic) throws IOException {
    sendToServer(new OnSiteLoginMessage(user, password, clinic, OnSiteLoginAction.LOGIN));
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

  //
  // ********************************* HANDLERS *********************************
  //

  @Override
  protected void handleMessageFromServer(Object message) {
    if (message instanceof OnSiteEntryMessage) {
      handleOnSiteEntryMessage((OnSiteEntryMessage) message);
    } else if (message instanceof OnSiteQueueMessage) {
      handleOnSiteQueueMessage((OnSiteQueueMessage) message);
    } else {
      super.handleMessageFromServer(message);
    }
  }

  private void handleOnSiteEntryMessage(OnSiteEntryMessage message) {
    EventBus.getDefault().post(OnSiteEntryEvent.entryResponseEvent(message.q_appt, message.patient, this));
  }

  private void handleOnSiteQueueMessage(OnSiteQueueMessage message) {
    if (message.rejection_reason != null) {
      EventBus.getDefault().post(PatientWalkInAppointmentEvent.newWalkInRejection(message.rejection_reason, this));
    } else {
      EventBus.getDefault().post(PatientWalkInAppointmentEvent.newWalkInResponse(message.q_appt, this));
    }
  }
}
