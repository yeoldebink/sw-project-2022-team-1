package il.cshaifa.hmo_system.on_site_client.gui.staff;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.utils.ClientUtils;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.StaffNextAppointmentEvent;
import il.cshaifa.hmo_system.on_site_client.events.ViewAppointmentEvent;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class StaffQueueController extends Controller {

  private final ReentrantLock queue_lock;
  private LocalDateTime queue_timestamp;

  public StaffQueueController(
      ViewController view_controller,
      Stage stage,
      List<QueuedAppointment> initial_queue,
      LocalDateTime queue_timestamp) {
    super(view_controller, stage);
    queue_lock = new ReentrantLock(true);
    this.queue_timestamp = queue_timestamp;
    Platform.runLater(
        () ->
            ((StaffQueueViewController) view_controller).populateAppointmentsTable(initial_queue));
  }

  @Subscribe
  public void onStaffNextAppointmentEvent(StaffNextAppointmentEvent event) {
    if (event.getSender().equals(this.view_controller)) {
      try {
        HMOOnSiteClient.getClient().staffQueuePopRequest();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    } else if (event.getSender().equals(HMOOnSiteClient.getClient())) {
      queue_lock.lock();
      if (queue_timestamp.isAfter(event.queue_timestamp)) return;
      try {
        ((StaffQueueViewController) view_controller).populateAppointmentsTable(event.updated_queue);
      } finally {
        queue_lock.unlock();
      }

      if (event.q_appt != null) {
        // popped appointment
        viewAppointment(event.q_appt, false);
      }
    }
  }

  @Subscribe
  public void onViewAppointmentEvent(ViewAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;
    viewAppointment(event.q_appt, true);
  }

  private void viewAppointment(QueuedAppointment q_appt, boolean readonly) {
    var loader =
        new FXMLLoader(
            getClass().getResource(ClientUtils.get_fxml(StaffAppointmentViewController.class)));
    loader.setControllerFactory(c -> new StaffAppointmentViewController(q_appt, readonly));
    Platform.runLater(
        () -> {
          try {
            ClientUtils.openNewWindow(
                StaffAppointmentViewController.class,
                StaffAppointmentController.class,
                loader,
                false);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        });
  }
}
