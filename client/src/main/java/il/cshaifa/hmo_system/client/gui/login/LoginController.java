package il.cshaifa.hmo_system.client.gui.login;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.LoginEvent;
import il.cshaifa.hmo_system.client.events.LoginEvent.Response;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.ManagerDashboardController;
import il.cshaifa.hmo_system.client.gui.manager_dashboard.ManagerDashboardViewController;
import il.cshaifa.hmo_system.client.utils.Utils;
import il.cshaifa.hmo_system.entities.User;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.greenrobot.eventbus.Subscribe;

public class LoginController extends Controller {

  public LoginController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
  }

  /**
   * Event to handle the user request to login to the system
   *
   * @param event Holds the user input to the login screen
   */
  @Subscribe
  public void OnLoginRequestEvent(LoginEvent event) {
    if (event.getSender().equals(this.view_controller)) {
      try {
        String pass = event.password.equals("") ? null : event.password;
        HMOClient.getClient().loginRequest(event.id, pass);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Event to handle the response from the client about hte login request.
   *
   * @param event Hold the server response for the login
   * @throws Exception Thrown when there is an error opening the main window for the user
   */
  @Subscribe
  public void OnLoginRequestResponse(LoginEvent event) throws Exception {
    if (event.getSender().equals(HMOClient.getClient())) {
      if (event.response == Response.REJECT) {
        incorrectUser();
      } else if (event.response == Response.AUTHORIZE) {
        openMainScreenByRole(event.userData);
        Platform.runLater(() -> this.stage.close());
      }
    }
  }

  /** Show an error message to the user when an incorrect info is entered */
  private void incorrectUser() {
    // Letting the controller to call this function on the UI thread, and apply the changes
    Platform.runLater(() -> ((LoginViewController) view_controller).setFailedText());
  }

  /**
   * Handle which main screen we want to show by user role
   *
   * @param user The user that logged in to the system
   * @throws Exception Thrown when opening the screen failed
   */
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
            ManagerDashboardViewController.class, ManagerDashboardController.class, loader, true);

        break;
      default:
        throw new NotImplementedException("Only manager implemented");
    }
  }
}
