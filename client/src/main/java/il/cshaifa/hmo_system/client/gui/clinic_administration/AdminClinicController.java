package il.cshaifa.hmo_system.client.gui.clinic_administration;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.events.EditClinicEvent;
import il.cshaifa.hmo_system.client.events.EditClinicEvent.Phase;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AdminClinicController extends Controller {

  public AdminClinicController(ViewController view_controller) {
    super(view_controller);
    EventBus.getDefault().register(this);
  }

  @Subscribe
  @Override
  public void OnWindowCloseEvent(CloseWindowEvent event) {
    if (event.getViewControllerInstance().equals(view_controller))
      EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void updateClinicRequested(EditClinicEvent event) {
    if (event.phase == Phase.OPEN_WINDOW) return;

    var client = HMOClient.getClient();
    try {
      client.updateClinic(event.clinic);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
