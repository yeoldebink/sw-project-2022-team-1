package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddDoctorAppointmentsController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment.AddDoctorAppointmentsViewController;
import il.cshaifa.hmo_system.client.utils.Utils;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.AdminAppointmentMessageType;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AppointmentListController extends Controller {

  public AppointmentListController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    EventBus.getDefault().register(this);
    User staff_member =
        new User(((AppointmentListViewController) this.view_controller).staff_member);
    try {
      HMOClient.getClient().getStaffAppointments(staff_member);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onAppointmentsAdded(AddAppointmentEvent event) {
    if (!event.senderInstance.equals(HMOClient.getClient())|| event.response_type != AdminAppointmentMessageType.ACCEPT)
      return;
    User staff_member =
        new User(((AppointmentListViewController) this.view_controller).staff_member);
    try {
      HMOClient.getClient().getStaffAppointments(staff_member);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onShowAddAppointmentDialog(AddAppointmentEvent event) {
    if (!event.senderInstance.equals(this.view_controller)) return;
    var loader =
        new FXMLLoader(
            getClass()
                .getResource(ResourcePath.get_fxml(AddDoctorAppointmentsViewController.class)));

    loader.setControllerFactory(
        c -> {
          return new AddDoctorAppointmentsViewController(event.staff_member);
        });

    try {
      Utils.OpenNewWindow(
          AddDoctorAppointmentsViewController.class,
          AddDoctorAppointmentsController.class,
          loader,
          false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onAppointmentListReceived(AdminAppointmentListEvent event) {
    if (!event.senderInstance.equals(HMOClient.getClient())) return;

    var vc = ((AppointmentListViewController) this.view_controller);
    Platform.runLater(() -> vc.populateAppointmentsTable(event.appointments));
  }

  @Subscribe
  public void onDeleteAppointmentsRequest(AdminAppointmentListEvent event) {
    if (event.phase != AppointmentListEvent.Phase.DELETE) return;

    try {
      HMOClient.getClient().deleteAppointments(event.appointments);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {}
}
