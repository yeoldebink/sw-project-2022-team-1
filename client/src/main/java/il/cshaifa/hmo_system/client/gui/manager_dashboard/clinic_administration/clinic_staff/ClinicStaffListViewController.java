package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffMembersEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffMembersEvent.Phase;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import java.util.ArrayList;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;

public class ClinicStaffListViewController extends ViewController {

  @FXML private TableView<User> staff_table;
  @FXML private TableColumn<User, String> first_name;
  @FXML private TableColumn<User, String> last_name;
  @FXML private TableColumn<User, String> email;
  @FXML private TableColumn<User, String> phone;
  @FXML private TableColumn<User, Role> role;
  @FXML private TableColumn<User, Boolean> assigned;

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

    EventBus.getDefault().post(new AssignStaffMembersEvent(users, phase));
  }

  @FXML
  void showAppointmentListView() {
    User selected_staff_member = staff_table.getSelectionModel().getSelectedItem();

    EventBus.getDefault().post(new AppointmentListEvent(selected_staff_member));
  }

  void setCellValueFactory() {
    first_name.setCellValueFactory((new PropertyValueFactory<>("First_name")));
    last_name.setCellValueFactory((new PropertyValueFactory<>("Last_name")));
    email.setCellValueFactory((new PropertyValueFactory<>("Email")));
    phone.setCellValueFactory((new PropertyValueFactory<>("Phone")));
    role.setCellValueFactory((new PropertyValueFactory<>("Role")));
    assigned.setCellValueFactory((new PropertyValueFactory<>("Assigned")));
  }
}

class AssignedUser extends User {
  Boolean assigned;

  public AssignedUser(User user, Boolean assigned) {
    super(user);
    this.assigned = assigned;
  }

  public Boolean getAssigned() {
    return assigned;
  }
}
