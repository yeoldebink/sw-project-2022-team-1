package il.cshaifa.hmo_system.client.gui.patient_dashboard;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import javafx.stage.Stage;

public class PatientDashboardController extends Controller {

  public PatientDashboardController(
      ViewController view_controller, Stage stage) {
    super(view_controller, null);
  }

  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {
    super.onWindowCloseEvent(event);
  }
}
