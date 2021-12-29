package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.events.LoginEvent;
import il.cshaifa.hmo_system.client.events.WarningEvent;
import il.cshaifa.hmo_system.client.ocsf.AbstractClient;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.entities.Warning;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;

public class HMOClient extends AbstractClient {

  private static HMOClient client = null;
  public boolean msg = false;
  private User connected_user;
  private Patient connected_patient;

  private HMOClient(String host, int port) {
    super(host, port);
  }

  public User getConnected_user() {
    return connected_user;
  }

  public Patient getConnected_patient() {
    return connected_patient;
  }

  public static HMOClient getClient() {
    if (client == null) {
      client = new HMOClient("localhost", 3000);
    }
    return client;
  }

  /**
   * @param message the message sent. This message can be of several types, handled by controller.
   */
  @Override
  protected void handleMessageFromServer(Object message) {
    this.msg = true;
    if (message.getClass().equals(Warning.class)) {
      EventBus.getDefault().post(new WarningEvent((Warning) message));
    } else {
      if (message.getClass().equals(LoginMessage.class)) {
        this.connected_user = ((LoginMessage) message).user;
        this.connected_patient = ((LoginMessage) message).patient_data;
        handleLoginMessage((LoginMessage) message);
      } else if (message.getClass().equals(ClinicMessage.class)) {
        handleClinicMessage((ClinicMessage) message);
      }
      // EventBus.getDefault().post(message);
    }
  }

  // TODO Create ClinicEvent class and send it instead of message
  private void handleClinicMessage(ClinicMessage message) {
    EventBus.getDefault().post(message);
  }

  private void handleLoginMessage(LoginMessage message) {
    LoginEvent event = new LoginEvent(0, "");
    if (message.user == null) {
      event.phase = LoginEvent.Phase.REJECT;
    } else {
      event.id = message.id;
      event.password = message.password;
      event.phase = LoginEvent.Phase.AUTHORIZE;
      event.userData = message.user;
      event.patientData = message.patient_data;
    }
    EventBus.getDefault().post(event);
  }

  /**
   * Request from server a list of all clinics
   *
   * @throws IOException SQL exception
   */
  public void getClinics() throws IOException {
    client.sendToServer(new ClinicMessage());
  }

  /**
   * Receives a changed clinic object and updates it in DB
   *
   * @param clinic clinic to be updated at the DB
   * @throws IOException SQL exception
   */
  public void updateClinic(Clinic clinic) throws IOException {
    var clinic_message = new ClinicMessage(clinic);

    client.sendToServer(clinic_message);
    getClinics();
  }

  /**
   * @param user The id of the login request
   * @param password The password the user has entered
   * @throws IOException SQL exception
   */
  public void loginRequest(int user, String password) throws IOException {
    LoginMessage message = new LoginMessage(user, password);
    client.sendToServer(message);
  }
}
