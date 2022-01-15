package il.cshaifa.hmo_system.client.gui.patient_dashboard.patient_history;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client.events.PatientAppointmentListEvent;
import il.cshaifa.hmo_system.client.events.PatientAppointmentListEvent.Status;
import java.io.IOException;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class PatientAppointmentHistoryListController extends Controller {
  public PatientAppointmentHistoryListController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);

    try {
      HMOClient.getClient().getPatientHistory();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onPatientHistoryRespond(AppointmentListEvent event){
    if (!event.getSender().equals(HMOClient.getClient())) return;

    Platform.runLater(
        ()-> {
          ((PatientAppointmentHistoryListViewController) this.view_controller)
              .populateAppointmentsTable(event.appointments);
        }
    );
  }


  @Subscribe
  public void onCancelAppointmentRequest(PatientAppointmentListEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    try {
      HMOClient.getClient().cancelAppointment(event.appointments.get(0));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onCancelAppointmentRespond(PatientAppointmentListEvent event) {
    if (!event.getSender().equals(HMOClient.getClient())) return;

    if (event.status == Status.ACCEPTED) {
      System.out.println("Successfully canceled the appointment"); //TODO let user see this
      try {
        HMOClient.getClient().getPatientHistory();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (event.status == Status.REJECT) {
      System.out.println("NOTIFY USER OF DEATH"); //TODO let user see this
    }
  }

  @Subscribe
  public void onShowAppointmentDetailsRequest(PatientAppointmentListEvent event) {
    if (!event.getSender().equals(this.view_controller)
        || event.status != Status.SHOW_APPOINTMENT_DATA) return;
    //TODO need to create the view to see the appointment details
  }
}
