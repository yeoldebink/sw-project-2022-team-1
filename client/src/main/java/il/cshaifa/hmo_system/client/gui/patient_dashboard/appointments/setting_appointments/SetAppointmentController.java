package il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments.setting_appointments;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import javafx.stage.Stage;

public class SetAppointmentController extends Controller {

  private static SetAppointmentController instance;

  private SetAppointmentController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  public static SetAppointmentController getInstance() {
    return instance;
  }

  public static void create(ViewController view_controller, Stage stage) {
    if (instance != null) return;
    instance = new SetAppointmentController(view_controller, stage);
  }

  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {
    super.onWindowCloseEvent(event);
    instance = null;
  }
}
