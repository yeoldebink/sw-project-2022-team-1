package il.cshaifa.hmo_system.client.gui.clinic_administration.manager_dashboard;

import il.cshaifa.hmo_system.client.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ManagerDashboardViewController extends RoleDefinedViewController {

  @FXML private TabPane tabPane;
  @FXML private Tab staffAdministrationTab;
  @FXML private Tab clinicAdministrationTab;
  @FXML private Tab reportsTab;

  public ManagerDashboardViewController(User user) {
    super(user.getRole());
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
}
