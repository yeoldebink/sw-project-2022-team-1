package il.cshaifa.hmo_system.client.gui.patient_dashboard.clinic_view;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class MyClinicController extends Controller {

  private static MyClinicController instance;

  private MyClinicController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  public static MyClinicController getInstance() {
    return instance;
  }

  public static void create(ViewController view_controller, Stage stage) {
    if (instance != null && instance.view_controller != null) return;
    instance = new MyClinicController(view_controller, stage);
  }

  // this class will never be posted as an event
  // method is necessary to avoid an EventBus exception for lack of subscribed class method
  @Subscribe
  public void dummy(MyClinicController event) {}
}
