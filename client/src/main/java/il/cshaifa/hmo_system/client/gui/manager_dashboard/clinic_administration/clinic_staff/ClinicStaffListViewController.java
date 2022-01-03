package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent.Phase;
import il.cshaifa.hmo_system.entities.User;
import java.util.ArrayList;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import org.greenrobot.eventbus.EventBus;

public class ClinicStaffListViewController extends ViewController {

  @FXML private ContextMenu contextMenu;
  @FXML private MenuItem assignMenuItem;
  @FXML private MenuItem unassignMenuItem;
  @FXML private MenuItem showAppointmentsMenuItem;

  @FXML private TableView<AssignedUser> staff_table;
  @FXML private TableColumn<AssignedUser, String> first_name;
  @FXML private TableColumn<AssignedUser, String> last_name;
  @FXML private TableColumn<AssignedUser, String> email;
  @FXML private TableColumn<AssignedUser, String> phone;
  @FXML private TableColumn<AssignedUser, String> role;
  @FXML private TableColumn<AssignedUser, Boolean> assigned;

  @FXML
  public void initialize() {
    setCellValueFactory();
  }

  void populateStaffTable(Map<User, Boolean> staff_assignments) {

    ArrayList<AssignedUser> assigned_staff = new ArrayList<AssignedUser>();

    for (Map.Entry<User, Boolean> entry : staff_assignments.entrySet()) {
      assigned_staff.add(new AssignedUser(entry.getKey(), entry.getValue()));
    }

    staff_table.getItems().setAll(assigned_staff);
  }

  @FXML
  void assignSelectedStaffMembers(ActionEvent event) {
    assignOrUnassignSelectedStaffMembers(Phase.ASSIGN);
  }

  @FXML
  void unassignSelectedStaffMembers(ActionEvent event) {
    assignOrUnassignSelectedStaffMembers(Phase.UNASSIGN);
  }

  void assignOrUnassignSelectedStaffMembers(Phase phase) {
    ArrayList<User> users = new ArrayList<User>(staff_table.getSelectionModel().getSelectedItems());

    EventBus.getDefault().post(new AssignStaffEvent(users, phase));
  }

  @FXML
  void showAppointmentListView() {
    contextMenu.hide();
    User selected_staff_member = staff_table.getSelectionModel().getSelectedItem();

    EventBus.getDefault()
        .post(
            new AdminAppointmentListEvent(
                selected_staff_member, null, AdminAppointmentListEvent.Phase.OPEN_WINDOW));
  }

  void setCellValueFactory() {
    first_name.setCellValueFactory((new PropertyValueFactory<>("FirstName")));
    last_name.setCellValueFactory((new PropertyValueFactory<>("LastName")));
    email.setCellValueFactory((new PropertyValueFactory<>("Email")));
    phone.setCellValueFactory((new PropertyValueFactory<>("Phone")));
    role.setCellValueFactory((new PropertyValueFactory<>("RoleName")));
    assigned.setCellValueFactory((new PropertyValueFactory<>("Assigned")));
  }

  @FXML
  void contextMenuRequested(ContextMenuEvent contextMenuEvent) {
    AssignedUser selected_staff_member = staff_table.getSelectionModel().getSelectedItem();

    if (selected_staff_member.getAssigned()) {
      assignMenuItem.setVisible(false);
      unassignMenuItem.setVisible(true);
      showAppointmentsMenuItem.setVisible(true);
    } else {
      assignMenuItem.setVisible(true);
      unassignMenuItem.setVisible(false);
      showAppointmentsMenuItem.setVisible(false);
    }
  }
}
