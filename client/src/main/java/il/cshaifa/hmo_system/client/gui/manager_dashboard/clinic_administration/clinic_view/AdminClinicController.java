package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_view;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import java.io.IOException;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class AdminClinicController extends Controller {

  public AdminClinicController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  @Subscribe
  public void onRequestClinicUpdate(ClinicEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    var client = HMOClient.getClient();
    try {
      client.updateClinic(event.clinic);
      client.getClinics();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
