package il.cshaifa.hmo_system.client.gui.manager_dashboard;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import il.cshaifa.hmo_system.client.events.ClinicEvent.Phase;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_list_view.AdminClinicListController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.ClinicStaffListController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view.ReportListController;
import java.io.IOException;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ManagerDashboardController extends Controller {
  private final AdminClinicListController adminClinicListController;
  private final ClinicStaffListController clinicStaffListController;
  private final ReportListController reportListController;

  public ManagerDashboardController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    EventBus.getDefault().register(this);
    adminClinicListController =
        new AdminClinicListController(
            ((ManagerDashboardViewController) view_controller).getAdminClinicListViewController());

    clinicStaffListController =
        new ClinicStaffListController(
            ((ManagerDashboardViewController) view_controller).getClinicStaffListViewController());

    reportListController =
        new ReportListController(
            ((ManagerDashboardViewController) view_controller).getReportListViewController());

    try {
      HMOClient.getClient().getClinics();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onClinicsReceived(ClinicEvent event){
    if(event.phase != Phase.LIST) return;

    adminClinicListController.updateClinics(event.receivedClinics);
    reportListController.updateClinics(event.receivedClinics);
  }

  @Subscribe
  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {
    if (event.getViewControllerInstance().equals(this.view_controller))
      EventBus.getDefault().unregister(this);
  }
}
