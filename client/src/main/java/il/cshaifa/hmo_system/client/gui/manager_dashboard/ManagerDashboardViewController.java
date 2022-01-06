package il.cshaifa.hmo_system.client.gui.manager_dashboard;

import il.cshaifa.hmo_system.client.Utils;
import il.cshaifa.hmo_system.client.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import il.cshaifa.hmo_system.client.events.ClinicEvent.Phase;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_list_view.AdminClinicListViewController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.ClinicStaffListViewController;
import il.cshaifa.hmo_system.entities.User;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.greenrobot.eventbus.EventBus;

public class ManagerDashboardViewController extends RoleDefinedViewController {

  @FXML private Menu myClinicMenu;

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

    clinic_list.getKey().prefWidthProperty().bind(tabPane.widthProperty());
    clinic_list.getKey().prefHeightProperty().bind(tabPane.heightProperty());
    clinicAdministrationTab.setContent(clinic_list.getKey());
    adminClinicListViewController = (AdminClinicListViewController) clinic_list.getValue();

    clinic_staff_list.getKey().prefWidthProperty().bind(tabPane.widthProperty());
    clinic_staff_list.getKey().prefHeightProperty().bind(tabPane.heightProperty());
    staffAdministrationTab.setContent(clinic_staff_list.getKey());
    clinicStaffListViewController = (ClinicStaffListViewController) clinic_staff_list.getValue();

    applyRoleBehavior();
  }

  @FXML
  public void editMyClinicHours(ActionEvent event) {
    ClinicEvent clinic_event = new ClinicEvent(null, Phase.EDIT);
    EventBus.getDefault().post(clinic_event);
  }

  @Override
  protected void applyRoleBehavior() {
    if (role.getName().equals("Clinic Manager")) tabPane.getTabs().remove(clinicAdministrationTab);
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
}
