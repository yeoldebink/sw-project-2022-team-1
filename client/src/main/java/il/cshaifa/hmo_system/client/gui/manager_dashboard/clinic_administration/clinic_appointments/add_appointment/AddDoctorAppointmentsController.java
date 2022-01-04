package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent.Phase;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.AdminAppointmentMessageType;
import java.io.IOException;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AddDoctorAppointmentsController extends Controller {

  public AddDoctorAppointmentsController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    EventBus.getDefault().register(this);
  }

  @Subscribe
  public void addAppointments(AddAppointmentEvent event) {
    if (event.phase != Phase.SEND) return;

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

  @Subscribe
  public void onAppointmentCreationResponse(AddAppointmentEvent event) {
    if (event.phase != Phase.RECEIVE) return;
    else if (event.response_type == AdminAppointmentMessageType.REJECT) {
      // TODO: DO SOMETHING
    } else {
      // TODO: DO SOMETHING ELSE
      Platform.runLater(() -> stage.close());
    }
  }

  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {}
}
