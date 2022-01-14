package il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments.setting_appointments.SetAppointmentController;
import il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments.setting_appointments.SetAppointmentViewController;
import il.cshaifa.hmo_system.client.utils.Utils;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class ChooseAppointmentTypeController extends Controller {

  private static ChooseAppointmentTypeController instance;

  private ChooseAppointmentTypeController(
      ViewController view_controller,
      Stage stage) {
    super(view_controller, null);
  }

  public ChooseAppointmentTypeController getInstance() {
    return instance;
  }

  public static void create(ViewController view_controller, Stage stage) {
    if (instance != null) return;
    instance = new ChooseAppointmentTypeController(view_controller, stage);
  }
}
