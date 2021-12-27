package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import org.greenrobot.eventbus.EventBus;

public class ClinicStaffListController extends Controller {
  public ClinicStaffListController(ViewController view_controller) {
    super(view_controller);
    EventBus.getDefault().register(this);
  }



  @Override
  public void OnWindowCloseEvent(CloseWindowEvent event) {
    if(event.getViewControllerInstance().equals(this.view_controller))
      EventBus.getDefault().unregister(this);
  }
}
