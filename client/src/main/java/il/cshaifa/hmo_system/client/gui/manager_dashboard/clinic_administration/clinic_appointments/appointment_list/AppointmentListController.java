package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.client.Utils;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent.Phase;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddAppointmentController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddAppointmentViewController;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AppointmentListController extends Controller {

  public AppointmentListController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    EventBus.getDefault().register(this);
  }

  @Subscribe
  public void onAddAppointmentsWindowOpened(AddAppointmentEvent event) throws Exception {
    if (event.phase != Phase.OPEN_WINDOW) return;
    var loader =
        new FXMLLoader(
            getClass().getResource(ResourcePath.get_fxml(AddAppointmentViewController.class)));

    loader.setControllerFactory(c -> {return new AddAppointmentViewController(event.staff_member, event.clinic);});

    Utils.OpenNewWindow(AddAppointmentViewController.class, AddAppointmentController.class, loader,
        false);
  }

  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {}
}
