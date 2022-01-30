package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard;

import il.cshaifa.hmo_system.client_base.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.client_base.events.ClinicEvent;
import il.cshaifa.hmo_system.client_base.utils.ClientUtils;
import il.cshaifa.hmo_system.desktop_client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_list_view.AdminClinicListViewController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_staff.ClinicStaffListViewController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.report_view.ReportListViewController;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifa.hmo_system.Constants.APPT_TYPE;
import static il.cshaifa.hmo_system.Constants.CLINIC_MANAGER;
import static il.cshaifa.hmo_system.Constants.COVID_TEST;
import static il.cshaifa.hmo_system.Constants.COVID_VACCINE;
import static il.cshaifa.hmo_system.Constants.FLU_VACCINE;
import static il.cshaifa.hmo_system.Constants.ROLE;

public class ManagerDashboardViewController extends RoleDefinedViewController {

  @FXML private Menu myClinicMenu;

  @FXML private TabPane tabPane;

  @FXML private Tab staffAdministrationTab;
  @FXML private Tab clinicAdministrationTab;
  @FXML private Tab reportsTab;

  @FXML private AdminClinicListViewController adminClinicListViewController;
  @FXML private ClinicStaffListViewController clinicStaffListViewController;
  @FXML private ReportListViewController reportListViewController;

  public ManagerDashboardViewController(User user) {
    super(user.getRole());
  }

  @FXML
  public void initialize() throws IOException {
    var clinic_list = ClientUtils.loadFXML(getClass(), AdminClinicListViewController.class);
    var clinic_staff_list = ClientUtils.loadFXML(getClass(), ClinicStaffListViewController.class);
    var report_list =
        ClientUtils.loadFXML(
            getClass(), ReportListViewController.class, c -> new ReportListViewController(role));

    clinic_list.pane.prefWidthProperty().bind(tabPane.widthProperty());
    clinic_list.pane.prefHeightProperty().bind(tabPane.heightProperty());
    clinicAdministrationTab.setContent(clinic_list.pane);
    adminClinicListViewController = (AdminClinicListViewController) clinic_list.view_controller;

    clinic_staff_list.pane.prefWidthProperty().bind(tabPane.widthProperty());
    clinic_staff_list.pane.prefHeightProperty().bind(tabPane.heightProperty());
    staffAdministrationTab.setContent(clinic_staff_list.pane);
    clinicStaffListViewController =
        (ClinicStaffListViewController) clinic_staff_list.view_controller;

    reportsTab.setContent(report_list.pane);
    reportListViewController = (ReportListViewController) report_list.view_controller;

    applyRoleBehavior();
  }

  /**
   * Emits event requesting to open the edit clinic dialog
   * @param event
   */
  @FXML
  public void editMyClinicHours(ActionEvent event) {
    ClinicEvent clinic_event = new ClinicEvent((Clinic) null, this);
    EventBus.getDefault().post(clinic_event);
  }

  @Override
  protected void applyRoleBehavior() {
    if (role.equals(ROLE(CLINIC_MANAGER))) tabPane.getTabs().remove(clinicAdministrationTab);
    else {
      tabPane.getTabs().remove(staffAdministrationTab);
      myClinicMenu.setVisible(false);
    }
  }

  public AdminClinicListViewController getAdminClinicListViewController() {
    return adminClinicListViewController;
  }

  public ClinicStaffListViewController getClinicStaffListViewController() {
    return clinicStaffListViewController;
  }

  public ReportListViewController getReportListViewController() {
    return reportListViewController;
  }


  /**
   * Emits event requesting to open add appointment view of Flu Vaccine appointment type
   * @param actionEvent
   */
  public void addFluVaccineAppts(ActionEvent actionEvent) {
    addTestOrVaccineAppointments(APPT_TYPE(FLU_VACCINE));
  }

  /**
   * Emits event requesting to open add appointment view of COVID Vaccine appointment type
   * @param actionEvent
   */
  public void addCovidVaccineAppts(ActionEvent actionEvent) {
    addTestOrVaccineAppointments(APPT_TYPE(COVID_VACCINE));
  }

  /**
   * Emits event requesting to open add appointment view of COVID Test appointment type
   * @param actionEvent
   */
  public void addCovidTests(ActionEvent actionEvent) {
    addTestOrVaccineAppointments(APPT_TYPE(COVID_TEST));
  }

  private void addTestOrVaccineAppointments(AppointmentType appointmentType) {
    var event = new AddAppointmentEvent(null, null, null, this);
    event.type = appointmentType;
    EventBus.getDefault().post(event);
  }
}
