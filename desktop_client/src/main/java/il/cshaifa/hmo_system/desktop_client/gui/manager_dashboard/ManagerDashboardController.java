package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.events.ClinicEvent;
import il.cshaifa.hmo_system.client_base.utils.ClientUtils;
import il.cshaifa.hmo_system.desktop_client.HMODesktopClient;
import il.cshaifa.hmo_system.desktop_client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.desktop_client.events.ClinicStaffEvent;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddAppointmentsController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddAppointmentsViewController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_list_view.AdminClinicListController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_staff.ClinicStaffListController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_view.AdminClinicController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_view.AdminClinicViewController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.report_view.ReportListController;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifa.hmo_system.Constants.HMO_MANAGER;
import static il.cshaifa.hmo_system.Constants.ROLE;

public class ManagerDashboardController extends Controller {
  private final AdminClinicListController adminClinicListController;
  private final ClinicStaffListController clinicStaffListController;
  private final ReportListController reportListController;

  public ManagerDashboardController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);

    var user = HMODesktopClient.getClient().getConnected_user();

    stage.setTitle(
        String.format("Management Console ~ %s", user));

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
      HMODesktopClient.getClient().getClinics();
      HMODesktopClient.getClient().getStaff();
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
    if (!event.getSender().equals(HMODesktopClient.getClient())) return;

    adminClinicListController.updateClinics(event.receivedClinics);
    reportListController.updateClinics(event.receivedClinics);
  }

  @Subscribe
  public void onClinicStaffReceived(ClinicStaffEvent event) {
    if (!event.getSender().equals(HMODesktopClient.getClient())) return;
    ArrayList<ClinicStaff> clinicStaff = new ArrayList<>();

    var role = HMODesktopClient.getClient().getConnected_user().getRole();
    if (role.equals(ROLE(HMO_MANAGER))) {
      var unique_staffs_map = new HashMap<String, ClinicStaff>();
      for (var member : event.clinic_staff) {
        var name = member.getUser().toString();
        unique_staffs_map.put(name, member);
      }
      clinicStaff.addAll(new ArrayList<>(unique_staffs_map.values()));
    } else {
      var clinic_manager_id = HMODesktopClient.getClient().getConnected_user().getId();
      for (var staff_member : event.clinic_staff) {
        var staff_manager =
            staff_member.getClinic() == null ? null : staff_member.getClinic().getManager_user();

        if (staff_manager != null && staff_manager.getId() == clinic_manager_id) {
          clinicStaff.add(staff_member);
        }
      }
    }
    clinicStaff.sort(Comparator.comparing(ClinicStaff::toString));
    reportListController.updateStaffMembers(clinicStaff);
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
            getClass().getResource(ClientUtils.get_fxml(AdminClinicViewController.class)));

    // this is for the HMO manager
    if (event.clinic == null) {
      event.clinic = HMODesktopClient.getClient().getConnected_employee_clinics().get(0);
    }

    loader.setControllerFactory(
        c -> {
          return new AdminClinicViewController(
              event.clinic, HMODesktopClient.getClient().getConnected_user().getRole());
        });
    try {
      ClientUtils.openNewWindow(
          AdminClinicViewController.class, AdminClinicController.class, loader, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onTestOrVaccineAddingRequest(AddAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    ClientUtils.openNewSingletonWindow(
        AddAppointmentsViewController.class,
        AddAppointmentsController.class,
        false,
        c -> new AddAppointmentsViewController(event.type));
  }
}
