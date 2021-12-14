package il.cshaifa.OCSFHmo.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifa.OCSFHmo.client.ocsf.AbstractClient;
import il.cshaifa.OCSFHmo.entities.Warning;

public class HMOClient extends AbstractClient {

  private static HMOClient client = null;

  private HMOClient(String host, int port) {
    super(host, port);
  }

  @Override
  protected void handleMessageFromServer(Object msg) {
    if (msg.getClass().equals(Warning.class)) {
      EventBus.getDefault().post(new WarningEvent((Warning) msg));
    }

  }

  public static HMOClient getClient() {
    if (client == null) {
      client = new HMOClient("localhost", 3000);
    }
    return client;
  }

}