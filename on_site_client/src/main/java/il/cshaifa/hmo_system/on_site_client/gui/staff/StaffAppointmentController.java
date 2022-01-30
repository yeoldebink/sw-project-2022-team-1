package il.cshaifa.hmo_system.on_site_client.gui.staff;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.ViewAppointmentEvent;
import java.io.IOException;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class StaffAppointmentController extends Controller {

  public StaffAppointmentController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  @Subscribe
  public void onAppointmentCommentsEvent(ViewAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;
    try {
      HMOOnSiteClient.getClient().updateAppointmentComments(event.q_appt.appointment);
      stage.close();
      onWindowClose();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }
}
