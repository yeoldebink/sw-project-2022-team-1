package il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent.RequestType;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent.ResponseType;
import il.cshaifa.hmo_system.entities.AppointmentType;
import java.io.IOException;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class SetAppointmentController extends Controller {

  private static SetAppointmentController instance;

  private SetAppointmentController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  public static SetAppointmentController getInstance() {
    return instance;
  }

  public static void create(ViewController view_controller, Stage stage) {
    if (instance != null && instance.view_controller != null) return;
    instance = new SetAppointmentController(view_controller, stage);
  }

  private boolean patientIsMinor() {
    return HMOClient.getClient()
        .getConnected_patient()
        .getBirthday()
        .isAfter(LocalDateTime.now().minusYears(18));
  }

  @Subscribe
  public void onAppointmentsRequested(SetAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller) || event.request != null) return;
    try {
      switch (event.appointmentType.getName()) {
        case "Family Doctor":
          HMOClient.getClient()
              .getFamilyDoctorAppointments(
                  new AppointmentType(patientIsMinor() ? "Pediatrician" : "Family Doctor"));
          break;
        default:
          break;
      }
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  @Subscribe
  public void onAppointmentListEvent(AppointmentListEvent event) {
    Platform.runLater(
        () ->
            ((SetAppointmentViewController) view_controller)
                .populateAppointmentDates(event.appointments));
  }

  @Subscribe
  public void onAppointmentSelectionChanged(SetAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller) || event.request == null) return;
    try {
      switch (event.request) {
        case RELEASE:
          HMOClient.getClient().cancelAppointment(event.appointment);
          break;
        case LOCK:
          HMOClient.getClient().lockAppointment(event.appointment);
          break;
        case TAKE:
          HMOClient.getClient().takeAppointment(event.appointment);
          break;
        default:
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onResponseFromClient(SetAppointmentEvent event) {
    if (!event.getSender().equals(HMOClient.getClient())) return;
    if (event.request == RequestType.TAKE) {
      Platform.runLater(() -> ((SetAppointmentViewController) view_controller).takeAppointment(
          event.response == ResponseType.AUTHORIZE,
          (int) stage.getX() + 100,
          (int) stage.getY() + 100
      ));
    }
  }
}
