package il.cshaifa.hmo_system.client.gui.login;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;

public class LoginController extends Controller {

  public LoginController(ViewController view_controller) {
    super(view_controller);
  }

  @Override
  public void OnWindowCloseEvent(CloseWindowEvent event) {}
}
