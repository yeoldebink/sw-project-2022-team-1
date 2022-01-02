package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent.Phase;
import il.cshaifa.hmo_system.client.events.ClinicStaffEvent;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage.Type;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ClinicStaffListController extends Controller {

  public ClinicStaffListController(ViewController view_controller) {
    super(view_controller, null);
    EventBus.getDefault().register(this);

    try {
      HMOClient.getClient().getStaff();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles incoming clinicstaff events from the servers
   *
   * @param event
   */
  @Subscribe
  public void clinicStaffListReceived(ClinicStaffEvent event) {
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

  @Subscribe
  public void clinicStaffAssignmentRequestReceived(AssignStaffEvent event) {
    if (event.phase == Phase.RESPOND) {
      try {
        HMOClient.getClient().getStaff();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      StaffAssignmentMessage.Type type = event.phase == Phase.ASSIGN ? Type.ASSIGN : Type.UNASSIGN;
      try {
        // need to copy construct the users so the server doesn't throw a hissy fit over
        // AssignedUser
        ArrayList<User> staff_users = new ArrayList<>();
        for (var a_user : event.staff) {
          staff_users.add(new User(a_user));
        }

        HMOClient.getClient().assignOrUnassignStaff(staff_users, type);
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  }

  @Subscribe
  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {
    if (event.getViewControllerInstance().equals(this.view_controller))
      EventBus.getDefault().unregister(this);
  }
}
