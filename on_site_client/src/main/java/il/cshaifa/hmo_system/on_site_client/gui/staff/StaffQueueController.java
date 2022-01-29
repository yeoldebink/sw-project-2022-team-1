package il.cshaifa.hmo_system.on_site_client.gui.staff;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.StaffNextAppointmentEvent;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class StaffQueueController extends Controller {

  private final ReentrantLock queue_lock;
  private LocalDateTime queue_timestamp;

  public StaffQueueController(
      ViewController view_controller,
      Stage stage, List<QueuedAppointment> initial_queue, LocalDateTime queue_timestamp) {
    super(view_controller, stage);
    queue_lock = new ReentrantLock(true);
    this.queue_timestamp = queue_timestamp;
    Platform.runLater(() -> ((StaffQueueViewController) view_controller).populateAppointmentsTable(initial_queue));
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
      try {
        ((StaffQueueViewController) view_controller).populateAppointmentsTable(event.updated_queue);
      } finally {
        queue_lock.unlock();
      }
      // TODO see this appointment
    }
  }
}
