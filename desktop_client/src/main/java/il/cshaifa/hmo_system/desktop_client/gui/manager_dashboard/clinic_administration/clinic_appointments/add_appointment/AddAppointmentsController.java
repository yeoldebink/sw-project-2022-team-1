package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment;

import il.cshaifa.hmo_system.CommonEnums.AddAppointmentRejectionReason;
import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.desktop_client.HMODesktopClient;
import il.cshaifa.hmo_system.desktop_client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.User;
import java.io.IOException;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class AddAppointmentsController extends Controller {

  private static AddAppointmentsController instance;

  private AddAppointmentsController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  public static AddAppointmentsController getInstance() {
    return instance;
  }

  public static void create(ViewController view_controller, Stage stage) {
    if (instance != null && instance.view_controller != null) return;

    instance = new AddAppointmentsController(view_controller, stage);
  }

  /**
   * Event that is called when manager level user request to add appointments to a staff member
   *
   * @param event holding the params that are set in the GUI
   */
  @Subscribe
  public void addAppointments(AddAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;
    if (event.count <= 0) {
      Platform.runLater(
          () ->
              ((AddAppointmentsViewController) this.view_controller)
                  .setErrorMessage("The amount should be larger than 0"));

      return;
    }

    User staff_member = null;
    AppointmentType appt_type;
    if (event.staff_member != null) {
      if (event.staff_member.getRole().isSpecialist())
        appt_type = new AppointmentType("Specialist");
      else appt_type = new AppointmentType(event.staff_member.getRole().getName());
      staff_member = new User(event.staff_member);
    } else {
      appt_type = event.type;
    }
    try {
      HMODesktopClient.getClient()
          .createAppointments(staff_member, event.start_datetime, event.count, appt_type);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Event that handle the response from the client about the status of events that were created
   * Show info when there is a rejection with info why the creation was rejected
   *
   * @param event Holds all the params of the respond from the client
   */
  @Subscribe
  public void onAppointmentCreationResponse(AddAppointmentEvent event) {
    if (!event.getSender().equals(HMODesktopClient.getClient())) return;
    if (!event.success) {
      String rejectionMessage = "";
      if (event.reject == AddAppointmentRejectionReason.OVERLAPPING) {
        rejectionMessage = "Staff member is busy at this time";
      } else if (event.reject == AddAppointmentRejectionReason.IN_THE_PAST) {
        rejectionMessage = "Cannot open appointments in the past";
      }

      String finalRejectionMessage =
          rejectionMessage; // Java requested this... didn't like that I changed the value...
      Platform.runLater(
          () ->
              ((AddAppointmentsViewController) this.view_controller)
                  .setErrorMessage(finalRejectionMessage));
    } else {
      Platform.runLater(() ->
      {
        this.onWindowClose();
        stage.close();
      }
      );
    }
  }
}
