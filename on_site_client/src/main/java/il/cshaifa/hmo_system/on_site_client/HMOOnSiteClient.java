package il.cshaifa.hmo_system.on_site_client;

import il.cshaifa.hmo_system.client_base.HMOClient;

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

  //
  // ********************************* HANDLERS *********************************
  //
}
