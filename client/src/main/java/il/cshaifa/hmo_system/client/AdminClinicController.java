package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.EditClinicEvent;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AdminClinicController extends Controller {

  public AdminClinicController(ViewController view_controller) {
    super(view_controller);
    EventBus.getDefault().register(this);
  }

  @Subscribe
  public void updateClinicRequested(EditClinicEvent event) {
    var client = HMOClient.getClient();
    try {
      client.updateClinic(event.clinic);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
