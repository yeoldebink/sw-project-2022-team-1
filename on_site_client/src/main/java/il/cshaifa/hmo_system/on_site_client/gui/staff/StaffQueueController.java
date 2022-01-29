package il.cshaifa.hmo_system.on_site_client.gui.staff;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.StaffNextAppointmentEvent;
import java.io.IOException;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class StaffQueueController extends Controller {

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
      ((StaffQueueViewController) view_controller).populateAppointmentsTable(event.updated_queue);
      // TODO see this appointment
    }
  }
}
