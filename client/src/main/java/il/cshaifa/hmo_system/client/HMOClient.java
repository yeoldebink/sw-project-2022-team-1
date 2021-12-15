package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.events.ResponseEvent;
import il.cshaifa.hmo_system.client.events.WarningEvent;
import il.cshaifa.hmo_system.client.ocsf.AbstractClient;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Request;
import il.cshaifa.hmo_system.entities.Response;
import il.cshaifa.hmo_system.entities.Warning;
import java.io.IOException;
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

  public void getClinics() throws IOException {
    client.sendToServer(new Request(false, Clinic.class));
  }

  public void updateClinic(Clinic clinic) throws IOException {
    client.sendToServer(new Request(true, clinic));
    getClinics();
  }

  @Override
  protected void handleMessageFromServer(Object msg) {
    this.msg = true;
    if (msg.getClass().equals(Warning.class)) {
      EventBus.getDefault().post(new WarningEvent((Warning) msg));
    } else if (msg.getClass().equals(Response.class)) {
      EventBus.getDefault().post(new ResponseEvent((Response) msg));
    }
  }
}
