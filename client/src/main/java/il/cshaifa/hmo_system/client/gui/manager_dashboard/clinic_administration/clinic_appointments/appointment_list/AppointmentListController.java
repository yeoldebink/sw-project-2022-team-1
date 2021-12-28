package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;

public class AppointmentListController extends Controller {

  public AppointmentListController(ViewController view_controller) {
    super(view_controller);
  }

  @Override
  public void OnWindowCloseEvent(CloseWindowEvent event) {}
}
