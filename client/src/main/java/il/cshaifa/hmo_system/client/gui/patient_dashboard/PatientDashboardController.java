package il.cshaifa.hmo_system.client.gui.patient_dashboard;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments.ChooseAppointmentTypeController;
import il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments.ChooseAppointmentTypeViewController;
import il.cshaifa.hmo_system.client.utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class PatientDashboardController extends Controller {

  public PatientDashboardController(ViewController view_controller, Stage stage) {
    super(view_controller, null);

    //    ((PatientDashboardViewController) view_controller).updateNextAppointmentInfo(<appt
    // object>);
  }

  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {
    super.onWindowCloseEvent(event);
  }

  @Subscribe
  public void onSetAppointmentEvent(SetAppointmentEvent event) throws Exception {
    if (event.getSender() == this.view_controller) { // open the set appointments window
      var loader =
          new FXMLLoader(
              getClass()
                  .getResource(ResourcePath.get_fxml(ChooseAppointmentTypeViewController.class)));

      loader.setControllerFactory(
          c ->
              new ChooseAppointmentTypeViewController(
                  HMOClient.getClient().getConnected_patient()));

      Utils.openNewWindow(
          ChooseAppointmentTypeViewController.class,
          ChooseAppointmentTypeController.class,
          loader,
          false);
    } else {

    }
  }
}
