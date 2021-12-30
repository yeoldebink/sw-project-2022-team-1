package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import javafx.stage.Stage;

public class AppointmentListController extends Controller {

  public AppointmentListController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  @Override
  public void OnWindowCloseEvent(CloseWindowEvent event) {}
}
