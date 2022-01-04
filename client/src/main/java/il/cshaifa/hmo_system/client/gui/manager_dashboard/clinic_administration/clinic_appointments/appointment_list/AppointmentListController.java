package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.client.Utils;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent.Phase;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddDoctorAppointmentsController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddDoctorAppointmentsViewController;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AppointmentListController extends Controller {

  public AppointmentListController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    EventBus.getDefault().register(this);
    // TODO : pull the doctor's future appointments
  }

  @Subscribe
  public void onAddAppointmentsWindowOpened(AddAppointmentEvent event) throws Exception {
    if (event.phase != Phase.OPEN_WINDOW) return;
    var loader =
        new FXMLLoader(
            getClass()
                .getResource(ResourcePath.get_fxml(AddDoctorAppointmentsViewController.class)));

    loader.setControllerFactory(
        c -> {
          return new AddDoctorAppointmentsViewController(event.staff_member);
        });

    Utils.OpenNewWindow(
        AddDoctorAppointmentsViewController.class,
        AddDoctorAppointmentsController.class,
        loader,
        false);
  }

  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {}
}
