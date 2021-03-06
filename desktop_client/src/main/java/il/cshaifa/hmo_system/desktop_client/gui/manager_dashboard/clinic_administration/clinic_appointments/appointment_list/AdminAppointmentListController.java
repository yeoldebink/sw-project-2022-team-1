package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.utils.ClientUtils;
import il.cshaifa.hmo_system.desktop_client.HMODesktopClient;
import il.cshaifa.hmo_system.desktop_client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.desktop_client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddAppointmentsController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddAppointmentsViewController;
import il.cshaifa.hmo_system.entities.User;
import java.io.IOException;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class AdminAppointmentListController extends Controller {

  public AdminAppointmentListController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    User staff_member =
        new User(((AdminAppointmentListViewController) this.view_controller).staff_member);
    try {
      HMODesktopClient.getClient().getStaffAppointments(staff_member);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Event to listen to a response from the server when appointments are added to a member Updates
   * the appointment view with new appointments that were added
   *
   * @param event Holds info that appointments were added and request to get the update from the
   *     server
   */
  @Subscribe
  public void onAppointmentsAdded(AddAppointmentEvent event) {
    if (!event.getSender().equals(HMODesktopClient.getClient()) || !event.success) return;
    User staff_member =
        new User(((AdminAppointmentListViewController) this.view_controller).staff_member);
    try {
      HMODesktopClient.getClient().getStaffAppointments(staff_member);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Event to handle the user request to open the add appointment window for a staff member
   *
   * @param event Holds the GUI info
   */
  @Subscribe
  public void onShowAddAppointmentDialog(AddAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    ClientUtils.openNewSingletonWindow(
        AddAppointmentsViewController.class,
        AddAppointmentsController.class,
        false,
        c -> new AddAppointmentsViewController(event.staff_member));
  }

  /**
   * Event to handle the response from the client of appointment list received to populate the view
   * with the new appointments
   *
   * @param event Data from the client about the appointments.
   */
  @Subscribe
  public void onAppointmentListReceived(AdminAppointmentListEvent event) {
    if (!event.getSender().equals(HMODesktopClient.getClient())) return;

    var vc = ((AdminAppointmentListViewController) this.view_controller);
    Platform.runLater(() -> vc.populateAppointmentsTable(event.appointments));
  }

  /**
   * Event that handle the user request to delete an appointment from a staff member
   *
   * @param event Hold the GUI data
   */
  @Subscribe
  public void onDeleteAppointmentsRequest(AdminAppointmentListEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;
    try {
      HMODesktopClient.getClient().deleteAppointments(event.appointments);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
