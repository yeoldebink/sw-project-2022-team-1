package il.cshaifa.hmo_system.client.gui.manager_dashboard;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_list_view.AdminClinicListController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.ClinicStaffListController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_view.AdminClinicController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_view.AdminClinicViewController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view.ReportListController;
import il.cshaifa.hmo_system.client.utils.Utils;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class ManagerDashboardController extends Controller {
  private final AdminClinicListController adminClinicListController;
  private final ClinicStaffListController clinicStaffListController;
  private final ReportListController reportListController;

  public ManagerDashboardController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
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

  /**
   * Event to handle the client respond with a list of clinics
   *
   * @param event Data that holds the list of clinics in the server
   */
  @Subscribe
  public void onClinicsReceived(ClinicEvent event) {
    if (!event.getSender().equals(HMOClient.getClient())) return;

    adminClinicListController.updateClinics(event.receivedClinics);
    reportListController.updateClinics(event.receivedClinics);
  }

  /**
   * Event that handle the user request to edit its own clinic.
   *
   * @param event Data about the clinic we want to open the Edit Clinic view about clinic may be
   *     null only if user is a clinic manager
   */
  @Subscribe
  public void onEditMyClinicHours(ClinicEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    FXMLLoader loader =
        new FXMLLoader(
            getClass().getResource(ResourcePath.get_fxml(AdminClinicViewController.class)));

    // this is for the HMO manager
    if (event.clinic == null) {
      event.clinic = HMOClient.getClient().getConnected_employee_clinics().get(0);
    }

    loader.setControllerFactory(
        c -> {
          return new AdminClinicViewController(
              event.clinic, HMOClient.getClient().getConnected_user().getRole());
        });
    try {
      Utils.openNewWindow(
          AdminClinicViewController.class, AdminClinicController.class, loader, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
