package il.cshaifa.hmo_system.client.desktop.gui.patient_dashboard.green_pass;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class GreenPassController extends Controller {

  private static GreenPassController instance;

  private GreenPassController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  public static GreenPassController getInstance() {
    return instance;
  }

  public static void create(ViewController view_controller, Stage stage) {
    if (instance != null && instance.view_controller != null) return;
    instance = new GreenPassController(view_controller, stage);
  }

  @Subscribe
  public void dummy(GreenPassController event) {}
}
