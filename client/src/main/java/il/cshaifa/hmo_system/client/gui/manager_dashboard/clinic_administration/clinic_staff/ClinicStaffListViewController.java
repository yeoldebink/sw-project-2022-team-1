package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff;

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

public class ClinicStaffListViewController {

  @FXML private TableView<User> staffTable;
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

  private void setCellValueFactory() {
    first_name.setCellValueFactory((new PropertyValueFactory<>("First_name")));
    last_name.setCellValueFactory((new PropertyValueFactory<>("Last_name")));
    email.setCellValueFactory((new PropertyValueFactory<>("Email")));
    phone.setCellValueFactory((new PropertyValueFactory<>("Phone")));
    role.setCellValueFactory((new PropertyValueFactory<>("Role")));
    assigned.setCellValueFactory((new PropertyValueFactory<>("Assigned")));
  }

  void populateStaffTable(Map<User, Boolean> staff_assignments) {

    ArrayList<AssignedUser> assigned_staff = new ArrayList<AssignedUser>();

    for (Map.Entry<User, Boolean> entry : staff_assignments.entrySet()) {
      assigned_staff.add(new AssignedUser(entry.getKey(), entry.getValue()));
    }

    staffTable.getItems().setAll(assigned_staff);
  }

  @FXML
  public void assignSelectedStaffMembers(ActionEvent event) {
    assignOrUnassignSelectedStaffMembers(Phase.ASSIGN);
  }

  @FXML
  public void unassignSelectedStaffMembers(ActionEvent event) {
    assignOrUnassignSelectedStaffMembers(Phase.UNASSIGN);
  }

  void assignOrUnassignSelectedStaffMembers(Phase phase) {
    ArrayList<User> users = new ArrayList<User>(staffTable.getSelectionModel().getSelectedItems());

    EventBus.getDefault().post(new AssignStaffMembersEvent(users, phase));
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
