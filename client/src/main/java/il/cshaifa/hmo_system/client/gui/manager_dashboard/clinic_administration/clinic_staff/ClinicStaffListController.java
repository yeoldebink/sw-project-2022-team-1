package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent.StaffStatus;
import il.cshaifa.hmo_system.client.events.ClinicStaffEvent;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list.AppointmentListController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list.AppointmentListViewController;
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
import org.greenrobot.eventbus.EventBus;
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
   * Handles incoming clinicstaff events from the servers
   *
   * @param event
   */
  @Subscribe
  public void clinicStaffListReceived(ClinicStaffEvent event) {
    if (!event.senderInstance.equals(HMOClient.getClient())) return;
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
  public void onClinicStaffAssignmentRequest(AssignStaffEvent event) {
    if (!event.senderInstance.equals(this.view_controller)) return;

    try {
      var data = getClinicStaffAssignmentChange(event.staff, event.status);
      HMOClient.getClient().assignOrUnassignStaff(data.getKey(), data.getValue());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
    }

  private Pair<ArrayList<User>, Type> getClinicStaffAssignmentChange(ArrayList<AssignedUser> assignedUsers, StaffStatus status){
    StaffAssignmentMessage.Type type =
        status == StaffStatus.ASSIGN ? Type.ASSIGN : Type.UNASSIGN;
      // need to copy construct the users so the server doesn't throw a hissy fit over
      // AssignedUser
      ArrayList<User> staff_users = new ArrayList<>();
      for (var a_user : assignedUsers) {
        // this condition is true iff the user is assigned and the phase is unassign
        // or the other way around - that the user is unassigned and the phase is assign
        if (a_user.getAssigned() != (status == StaffStatus.ASSIGN)) {
          staff_users.add(new User(a_user));
        }
      }

      return new Pair<>(staff_users, type);
  }

  @Subscribe
  public void onClinicStaffAssignmentRespond(AssignStaffEvent event){
    if (event.senderInstance.equals(HMOClient.getClient())) {
      try {
        HMOClient.getClient().getStaff();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Subscribe
  public void onShowAppointmentListView(AdminAppointmentListEvent event) {
    if (!event.senderInstance.equals(this.view_controller)) return;
    FXMLLoader loader =
        new FXMLLoader(
            getClass().getResource(ResourcePath.get_fxml(AppointmentListViewController.class)));

    loader.setControllerFactory(
        c -> {
          return new AppointmentListViewController(
              event.staff_member, HMOClient.getClient().getConnected_employee_clinics().get(0));
        });

    try {
      Utils.OpenNewWindow(
          AppointmentListViewController.class, AppointmentListController.class, loader, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
