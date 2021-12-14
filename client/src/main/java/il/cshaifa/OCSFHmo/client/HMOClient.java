package il.cshaifa.OCSFHmo.client;
import il.cshaifa.OCSFHmo.client.ocsf.AbstractClient;

import il.cshaifa.OCSFHmo.entities.ClinicFunctionalities;
import il.cshaifa.OCSFHmo.entities.ServerFunctionalities;
import org.greenrobot.eventbus.EventBus;

public class HMOClient extends AbstractClient {

  private static HMOClient client = null;

  private HMOClient(String host, int port) {
    super(host, port);
  }

  /** Ask liran about this */
  @Override
  protected void handleMessageFromServer(Object response) {
    if (response.getClass().equals(ClinicFunctionalities.class)) {
      EventBus.getDefault().post(new WarningEvent((ServerFunctionalities) response));
    }
  }

  public static HMOClient getClient() {
    if (client == null) {
      client = new HMOClient("localhost", 3000);
    }
    return client;
  }
}
