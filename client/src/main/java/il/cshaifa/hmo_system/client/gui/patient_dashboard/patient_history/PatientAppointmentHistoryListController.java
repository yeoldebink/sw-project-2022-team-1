package il.cshaifa.hmo_system.client.gui.patient_dashboard.patient_history;

import il.cshaifa.hmo_system.client.HMODesktopClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client.events.PatientAppointmentListEvent;
import il.cshaifa.hmo_system.client.events.PatientAppointmentListEvent.Status;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent.ResponseType;
import java.io.IOException;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class PatientAppointmentHistoryListController extends Controller {

  private static PatientAppointmentHistoryListController instance;

  private PatientAppointmentHistoryListController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);

    try {
      HMODesktopClient.getClient().getPatientHistory();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static PatientAppointmentHistoryListController getInstance() {
    return instance;
  }

  public static void create(ViewController view_controller, Stage stage) {
    if (instance != null && instance.view_controller != null) return;
    instance = new PatientAppointmentHistoryListController(view_controller, stage);
  }

  @Subscribe
  public void onPatientHistoryRespond(AppointmentListEvent event) {
    if (!event.getSender().equals(HMODesktopClient.getClient())) return;

    Platform.runLater(
        () -> {
          ((PatientAppointmentHistoryListViewController) this.view_controller)
              .populateAppointmentsTable(event.appointments);
        });

    try {
      HMODesktopClient.getClient().getPatientNextAppointment();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  @Subscribe
  public void onCancelAppointmentRequest(PatientAppointmentListEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    try {
      HMODesktopClient.getClient().cancelAppointment(event.appointments.get(0));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onCancelAppointmentRespond(SetAppointmentEvent event) {
    if (!event.getSender().equals(HMODesktopClient.getClient())) return;

    if (event.response == ResponseType.AUTHORIZE) {
      try {
        HMODesktopClient.getClient().getPatientHistory();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      // TODO Notify user about failed cancellation
    }
  }

  @Subscribe
  public void onShowAppointmentDetailsRequest(PatientAppointmentListEvent event) {
    if (!event.getSender().equals(this.view_controller)
        || event.status != Status.SHOW_APPOINTMENT_DATA) return;
    // TODO need to create the view to see the appointment details
  }
}
