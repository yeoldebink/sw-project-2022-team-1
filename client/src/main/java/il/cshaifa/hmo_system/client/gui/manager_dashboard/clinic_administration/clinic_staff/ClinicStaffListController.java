package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent.Action;
import il.cshaifa.hmo_system.client.events.ClinicStaffEvent;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list.AdminAppointmentListController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list.AdminAppointmentListViewController;
import il.cshaifa.hmo_system.client.utils.Utils;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage.Type;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import javafx.fxml.FXMLLoader;
import javafx.util.Pair;
import org.greenrobot.eventbus.Subscribe;

public class ClinicStaffListController extends Controller {

  public ClinicStaffListController(ViewController view_controller) {
    super(view_controller, null);
    try {
      HMOClient.getClient().getStaff();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Event to handle the client response with clinic staff member list
   *
   * @param event Event data from the client with clinic staff
   */
  @Subscribe
  public void clinicStaffListReceived(ClinicStaffEvent event) {
    if (!event.getSender().equals(HMOClient.getClient())) return;
    var current_clinic_manager = HMOClient.getClient().getConnected_user();
    var assignment_map = new TreeMap<User, Boolean>(Comparator.comparing(User::getLastName));

    for (var clinic_staff_row : event.clinic_staff) {
      var staff_member = clinic_staff_row.getUser();
      var row_clinic_manager = clinic_staff_row.getClinic().getManager_user();

      // staff member either not present in the map or not assigned to this clinic,
      // set their value in the map to be the whether or not that row implies an
      // assignment to this clinic
      if (!assignment_map.containsKey(staff_member) || !assignment_map.get(staff_member)) {
        assignment_map.put(
            staff_member,
            row_clinic_manager != null
                && row_clinic_manager.getId() == current_clinic_manager.getId());
      }
    }

    ((ClinicStaffListViewController) this.view_controller).populateStaffTable(assignment_map);
  }

  /**
   * Event that handles user request to assign or unassign staff to their clinic
   *
   * @param event list of the users that we want to change their status
   */
  @Subscribe
  public void onClinicStaffAssignmentRequest(AssignStaffEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    try {
      var data = getClinicStaffAssignmentChange(event.staff, event.status);
      HMOClient.getClient().assignOrUnassignStaff(data.getKey(), data.getValue());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  /**
   * Create a list of pairs to know what status each staff member need to be after a user request
   *
   * @param assignedUsers list of the users we want to change status
   * @param status The status we want to set the users to
   * @return Pair of user list that need to change their status and the status to change to
   */
  private Pair<ArrayList<User>, Type> getClinicStaffAssignmentChange(
      ArrayList<AssignedUser> assignedUsers, Action status) {
    StaffAssignmentMessage.Type type = status == Action.ASSIGN ? Type.ASSIGN : Type.UNASSIGN;
    // need to copy construct the users so the server doesn't throw a hissy fit over
    // AssignedUser
    ArrayList<User> staff_users = new ArrayList<>();
    for (var a_user : assignedUsers) {
      // this condition is true iff the user is assigned and the phase is unassign
      // or the other way around - that the user is unassigned and the phase is assign
      if (a_user.getAssigned() != (status == Action.ASSIGN)) {
        staff_users.add(new User(a_user));
      }
    }

    return new Pair<>(staff_users, type);
  }

  /**
   * Event to handle response from client with a new clinic staff assigment change
   *
   * @param event Respond from the client that data has been updated
   */
  @Subscribe
  public void onClinicStaffAssignmentRespond(AssignStaffEvent event) {
    if (event.getSender().equals(HMOClient.getClient())) {
      try {
        HMOClient.getClient().getStaff();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Event that handle the user request for showing a specific staff member future appointments
   *
   * @param event Data from GUI about request staff member
   */
  @Subscribe
  public void onShowAppointmentListView(AdminAppointmentListEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;
    FXMLLoader loader =
        new FXMLLoader(
            getClass()
                .getResource(ResourcePath.get_fxml(AdminAppointmentListViewController.class)));

    loader.setControllerFactory(
        c -> {
          return new AdminAppointmentListViewController(event.staff_member);
        });

    try {
      Utils.OpenNewWindow(
          AdminAppointmentListViewController.class,
          AdminAppointmentListController.class,
          loader,
          true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
