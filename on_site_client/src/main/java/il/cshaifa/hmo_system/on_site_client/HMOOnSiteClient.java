package il.cshaifa.hmo_system.on_site_client;

import il.cshaifa.hmo_system.client_base.HMOClient;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.messages.DesktopLoginMessage;
import il.cshaifa.hmo_system.messages.OnSiteLoginMessage;
import il.cshaifa.hmo_system.messages.OnSiteLoginMessage.Action;
import java.io.IOException;

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
    sendToServer(new OnSiteLoginMessage(user, password, clinic, Action.LOGIN));
  }

  //
  // ********************************* HANDLERS *********************************
  //
}
