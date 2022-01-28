package il.cshaifa.hmo_system.on_site_client.gui.patient;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.utils.Utils;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.on_site_client.App;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.CloseStationEvent;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteEntryEvent;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteLoginEvent;
import il.cshaifa.hmo_system.on_site_client.gui.login.OnSiteLoginController;
import il.cshaifa.hmo_system.on_site_client.gui.login.OnSiteLoginViewController;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
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

  @Subscribe
  public void onOnSiteLoginEvent(OnSiteLoginEvent event) throws IOException {
    if (event.getSender().equals(this.view_controller)) {

      FXMLLoader loader =
          new FXMLLoader(App.class.getResource(Utils.get_fxml(OnSiteLoginViewController.class)));

      Scene scene = new Scene(loader.load());
      Stage stage = new Stage();
      stage.setScene(scene);
      OnSiteLoginController c = new OnSiteLoginController(loader.getController(), stage, event.action);

      stage.initModality(Modality.APPLICATION_MODAL);

      Platform.runLater(stage::show);
    }
  }

  @Subscribe
  public void onCloseStationEvent(CloseStationEvent event) {
    Platform.runLater(stage::close);
    onWindowClose();
  }

  private void printNumber(QueuedAppointment q_appt) {} // TODO implement me!
}
