package il.cshaifa.hmo_system.client.gui.patient_dashboard;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments.SetAppointmentController;
import il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments.SetAppointmentViewController;
import il.cshaifa.hmo_system.client.utils.Utils;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class PatientDashboardController extends Controller {

  public PatientDashboardController(ViewController view_controller, Stage stage) {
    super(view_controller, null);

    //    ((PatientDashboardViewController) view_controller).updateNextAppointmentInfo(<appt
    // object>);
  }

  @Override
  public void onWindowClose() {
    super.onWindowClose();
  }

  @Subscribe
  public void onSetAppointmentEvent(SetAppointmentEvent event) {
    if (event.getSender() == this.view_controller) { // open the set appointments window
      Utils.openNewSingletonWindow(
          SetAppointmentViewController.class,
          SetAppointmentController.class,
          false,
          c -> new SetAppointmentViewController(HMOClient.getClient().getConnected_patient()));
    }
  }
}
