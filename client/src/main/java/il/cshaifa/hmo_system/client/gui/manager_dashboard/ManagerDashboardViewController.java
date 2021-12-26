package il.cshaifa.hmo_system.client.gui.manager_dashboard;

import il.cshaifa.hmo_system.client.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_list_view.AdminClinicListViewController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.ClinicStaffListViewController;
import il.cshaifa.hmo_system.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    var loader =
        new FXMLLoader(
            ManagerDashboardViewController.class.getResource(
                ResourcePath.get_fxml(AdminClinicListViewController.class)));

    adminClinicListViewController = loader.getController();
  }

  @FXML
  public void initialize() {
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
