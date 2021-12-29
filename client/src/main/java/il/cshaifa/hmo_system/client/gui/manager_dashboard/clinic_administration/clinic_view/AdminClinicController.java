package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_view;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.events.EditClinicEvent;
import il.cshaifa.hmo_system.client.events.EditClinicEvent.Phase;
import java.io.IOException;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AdminClinicController extends Controller {

  public AdminClinicController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
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
