package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent.RejectionType;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.User;
import java.io.IOException;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class AddDoctorAppointmentsController extends Controller {

  public AddDoctorAppointmentsController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  /**
   * Event that is called when manager level user request to add appointments to a staff member
   *
   * @param event holding the params that are set in the GUI
   */
  @Subscribe
  public void addAppointments(AddAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    AppointmentType appt_type;
    if (event.staff_member.getRole().isSpecialist()) appt_type = new AppointmentType("Specialist");
    else appt_type = new AppointmentType(event.staff_member.getRole().getName());
    User staff_member = new User(event.staff_member);
    try {
      HMOClient.getClient()
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
    if (!event.getSender().equals(HMOClient.getClient())) return;
    if (!event.success) {
      String rejectionMessage = "";
      if (event.reject == RejectionType.OVERLAPPING) {
        rejectionMessage = "Staff member is busy at this time";
      } else if (event.reject == RejectionType.IN_THE_PAST) {
        rejectionMessage = "Cannot open appointments in the past";
      }

      String finalRejectionMessage =
          rejectionMessage; // Java requested this... didn't like that I changed the value...
      Platform.runLater(
          () ->
              ((AddDoctorAppointmentsViewController) this.view_controller)
                  .setErrorMessage(finalRejectionMessage));
    } else {
      Platform.runLater(() -> stage.close());
    }
  }
}
