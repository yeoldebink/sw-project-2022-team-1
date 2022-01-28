package il.cshaifa.hmo_system.on_site_client.gui.patient;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteEntryEvent;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.io.IOException;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class OnSitePatientController extends Controller {

  private Patient patient;

  public OnSitePatientController(
      ViewController view_controller,
      Stage stage) {
    super(view_controller, stage);
  }

  @Subscribe
  public void onEntryEvent(OnSiteEntryEvent event) {
    try {
      if (event.getSender().equals(this.view_controller)) {
        HMOOnSiteClient.getClient().patientEntryRequest(event.id);

      } else if (event.getSender().equals(HMOOnSiteClient.getClient())) {
        this.patient = event.patient;
        if (event.patient == null) {
          Platform.runLater(() -> ((OnSitePatientViewController) this.view_controller).invalidID());
        } else if (event.q_appt != null) printNumber(event.q_appt);
        else if (!event.patient.getHome_clinic().equals(HMOOnSiteClient.getClient().getStationClinic())) {
          Platform.runLater(() -> ((OnSitePatientViewController) this.view_controller).notInClinic(event.patient.getHome_clinic()));
        } else {
          Platform.runLater(() -> ((OnSitePatientViewController) this.view_controller).showDashboard(
              this.patient));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void printNumber(QueuedAppointment q_appt) {} // TODO implement me!
}
