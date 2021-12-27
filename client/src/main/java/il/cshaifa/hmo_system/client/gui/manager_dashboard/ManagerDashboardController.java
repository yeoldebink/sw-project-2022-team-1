package il.cshaifa.hmo_system.client.gui.manager_dashboard;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_list_view.AdminClinicListController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.ClinicStaffListController;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class ManagerDashboardController extends Controller {
  private AdminClinicListController adminClinicListController;
  private ClinicStaffListController clinicStaffListController;

  public ManagerDashboardController(ViewController view_controller) {
    super(view_controller);

    try {
      adminClinicListController =
          new AdminClinicListController(
              ((ManagerDashboardViewController) view_controller)
                  .getAdminClinicListViewController());
    } catch (IOException e) {
      e.printStackTrace();
    }
    clinicStaffListController =
        new ClinicStaffListController(
            ((ManagerDashboardViewController) view_controller).getClinicStaffListViewController());
  }

  @Override
  public void OnWindowCloseEvent(CloseWindowEvent event) {
    if(event.getViewControllerInstance().equals(this.view_controller))
      EventBus.getDefault().unregister(this);
  }
}
