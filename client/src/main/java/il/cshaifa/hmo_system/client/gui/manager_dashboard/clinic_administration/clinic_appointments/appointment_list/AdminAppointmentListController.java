package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddDoctorAppointmentsController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddDoctorAppointmentsViewController;
import il.cshaifa.hmo_system.client.utils.Utils;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.AdminAppointmentMessageType;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class AdminAppointmentListController extends Controller {

  public AdminAppointmentListController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    User staff_member =
        new User(((AdminAppointmentListViewController) this.view_controller).staff_member);
    try {
      HMOClient.getClient().getStaffAppointments(staff_member);
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
    if (!event.getSender().equals(HMOClient.getClient())
        || event.response_type != AdminAppointmentMessageType.ACCEPT) return;
    User staff_member =
        new User(((AdminAppointmentListViewController) this.view_controller).staff_member);
    try {
      HMOClient.getClient().getStaffAppointments(staff_member);
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
    var loader =
        new FXMLLoader(
            getClass()
                .getResource(ResourcePath.get_fxml(AddDoctorAppointmentsViewController.class)));

    loader.setControllerFactory(
        c -> {
          return new AddDoctorAppointmentsViewController(event.staff_member);
        });

    try {
      Utils.openNewWindow(
          AddDoctorAppointmentsViewController.class,
          AddDoctorAppointmentsController.class,
          loader,
          false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Event to handle the response from the client of appointment list received to populate the view
   * with the new appointments
   *
   * @param event Data from the client about the appointments.
   */
  @Subscribe
  public void onAppointmentListReceived(AdminAppointmentListEvent event) {
    if (!event.getSender().equals(HMOClient.getClient())) return;

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
      HMOClient.getClient().deleteAppointments(event.appointments);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
