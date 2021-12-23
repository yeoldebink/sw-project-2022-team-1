package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.events.WarningEvent;
import il.cshaifa.hmo_system.client.ocsf.AbstractClient;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Warning;
import il.cshaifa.hmo_system.messages.ClinicMessage;
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
      client = new HMOClient("localhost", 3010);
    }
    return client;
  }

  public void getClinics() throws IOException {
    client.sendToServer(new ClinicMessage());
  }

  public void updateClinic(Clinic clinic) throws IOException {
    var clinic_list = new ArrayList<Clinic>();
    clinic_list.add(clinic);
    var clinic_message = new ClinicMessage();
    clinic_message.clinics = clinic_list;

    client.sendToServer(clinic_message);
    getClinics();
  }

  @Override
  protected void handleMessageFromServer(Object msg) {
    this.msg = true;
    if (msg.getClass().equals(Warning.class)) {
      EventBus.getDefault().post(new WarningEvent((Warning) msg));
    } else {
      EventBus.getDefault().post(msg);
    }
  }
}
