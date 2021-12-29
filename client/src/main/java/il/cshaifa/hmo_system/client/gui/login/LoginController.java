package il.cshaifa.hmo_system.client.gui.login;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.Utils;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import il.cshaifa.hmo_system.client.events.LoginEvent;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.ManagerDashboardController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.ManagerDashboardViewController;
import il.cshaifa.hmo_system.entities.User;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginController extends Controller {

  public LoginController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    EventBus.getDefault().register(this);
  }

  @Subscribe
  @Override
  public void OnWindowCloseEvent(CloseWindowEvent event) {
    if (event.getViewControllerInstance().equals(this.view_controller))
      EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void OnLoginRequestEvent(LoginEvent event) {
    try {
      String pass = event.password.equals("") ? null : event.password;
      HMOClient.getClient().loginRequest(event.id, pass);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void OnLoginRequestResponse(LoginEvent event) throws Exception {
    if (event.phase == LoginEvent.Phase.REJECT) {
      incorrectUser();
    } else if (event.phase == LoginEvent.Phase.AUTHORIZE) {
      openMainScreenByRole(event.userData);
    }
  }

  private void incorrectUser() {
    // Letting the controller to call this function on the UI thread, and apply the changes
    Platform.runLater(() -> ((LoginViewController) view_controller).setFailedText());
  }

  private void openMainScreenByRole(User user) throws Exception {
    var role_name = user.getRole().getName();

    switch (role_name) {
      case ("Clinic Manager"):
      case ("HMO Manager"):
        FXMLLoader loader =
            new FXMLLoader(
                getClass()
                    .getResource(ResourcePath.get_fxml(ManagerDashboardViewController.class)));
        loader.setControllerFactory(
            c -> {
              return new ManagerDashboardViewController(user);
            });
        Utils.OpenNewWindow(
            ManagerDashboardViewController.class, ManagerDashboardController.class, loader);

        break;
      default:
        throw new NotImplementedException("Only manager implemented");
    }

    Platform.runLater(() -> this.stage.close());
  }
}
