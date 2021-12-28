package il.cshaifa.hmo_system.client.gui.manager_dashboard;

import il.cshaifa.hmo_system.client.Utils;
import il.cshaifa.hmo_system.client.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_list_view.AdminClinicListViewController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.ClinicStaffListViewController;
import il.cshaifa.hmo_system.entities.User;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ManagerDashboardViewController extends RoleDefinedViewController {

  @FXML private TabPane tabPane;

  @FXML private Tab staffAdministrationTab;
  @FXML private Tab clinicAdministrationTab;
  @FXML private Tab reportsTab;

  @FXML private AdminClinicListViewController adminClinicListViewController;
  @FXML private ClinicStaffListViewController clinicStaffListViewController;

  public ManagerDashboardViewController(User user) {
    super(user.getRole());
  }

  @FXML
  public void initialize() throws IOException {
    var clinic_list = Utils.loadFXML(getClass(), AdminClinicListViewController.class);
    var clinic_staff_list = Utils.loadFXML(getClass(), ClinicStaffListViewController.class);

    clinicAdministrationTab.setContent(clinic_list.getKey());
    adminClinicListViewController = (AdminClinicListViewController) clinic_list.getValue();

    staffAdministrationTab.setContent(clinic_staff_list.getKey());
    clinicStaffListViewController = (ClinicStaffListViewController) clinic_staff_list.getValue();

    applyRoleBehavior();
  }

  @Override
  protected void applyRoleBehavior() {
    if (role.getName().equals("Clinic Manager")) tabPane.getTabs().remove(clinicAdministrationTab);
    else tabPane.getTabs().remove(staffAdministrationTab);
  }

  public AdminClinicListViewController getAdminClinicListViewController() {
    return adminClinicListViewController;
  }

  public ClinicStaffListViewController getClinicStaffListViewController() {
    return clinicStaffListViewController;
  }
}
