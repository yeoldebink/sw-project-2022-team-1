package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.events.WarningEvent;
import il.cshaifa.hmo_system.client.ocsf.AbstractClient;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Warning;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import java.io.IOException;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;

public class HMOClient extends AbstractClient {

  private static HMOClient client = null;
  public boolean msg = false;

  private HMOClient(String host, int port) {
    super(host, port);
  }

  public static HMOClient getClient() {
    if (client == null) {
      client = new HMOClient("localhost", 3000);
    }
    return client;
  }


  /**
   * @param msg the message sent. This message can be of several types, handled by controller.
   */
  @Override
  protected void handleMessageFromServer(Object msg) {
    this.msg = true;
    if (msg.getClass().equals(Warning.class)) {
      EventBus.getDefault().post(new WarningEvent((Warning) msg));
    } else {
      EventBus.getDefault().post(msg);
    }
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
    var clinic_message = new ClinicMessage();
    var clinic_list = new ArrayList<Clinic>();
    clinic_list.add(clinic);
    clinic_message.clinics = clinic_list;

    client.sendToServer(clinic_message);
    getClinics();
  }

  /**
   * @param user     The id of the login request
   * @param password The password the user has entered
   * @throws IOException SQL exception
   */
  public void loginRequest(int user, String password) throws IOException {
    LoginMessage message = new LoginMessage(user, password);
    client.sendToServer(message);
  }
}
