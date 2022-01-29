package il.cshaifa.hmo_system.on_site_client.gui.staff;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.StaffNextAppointmentEvent;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class StaffQueueController extends Controller {

  private ReentrantLock queue_lock;

  public StaffQueueController(
      ViewController view_controller,
      Stage stage) {
    super(view_controller, stage);
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
        ((StaffQueueViewController) view_controller).populateAppointmentsTable(event.updated_queue,
            event.queue_timestamp);
      } finally {
        queue_lock.unlock();
      }
      // TODO see this appointment
    }
  }
}
