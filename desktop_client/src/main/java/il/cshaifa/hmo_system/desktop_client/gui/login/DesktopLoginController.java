package il.cshaifa.hmo_system.desktop_client.gui.login;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.events.LoginEvent;
import il.cshaifa.hmo_system.client_base.events.LoginEvent.Response;
import il.cshaifa.hmo_system.client_base.utils.ClientUtils;
import il.cshaifa.hmo_system.desktop_client.HMODesktopClient;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.ManagerDashboardController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.ManagerDashboardViewController;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.PatientDashboardController;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.PatientDashboardViewController;
import il.cshaifa.hmo_system.entities.User;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifa.hmo_system.Constants.CLINIC_MANAGER;
import static il.cshaifa.hmo_system.Constants.HMO_MANAGER;
import static il.cshaifa.hmo_system.Constants.PATIENT;

public class DesktopLoginController extends Controller {

  public DesktopLoginController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    stage.setTitle("HMO System ~ Login");
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
        HMODesktopClient.getClient().loginRequest(event.id, pass);
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
    if (event.getSender().equals(HMODesktopClient.getClient())) {
      if (event.response == Response.REJECT) {
        incorrectUser();
      } else if (event.response == Response.LOGGED_IN) {
        alreadyLoggedInUser();
      } else if (event.response == Response.AUTHORIZE) {
        openMainScreenByRole(event.userData);
        Platform.runLater(() -> this.stage.close());
        onWindowClose();
      }
    }
  }

  private void alreadyLoggedInUser() {
    Platform.runLater(
        () ->
            ((DesktopLoginViewController) this.view_controller)
                .setFailedText("This user is already logged in"));
  }

  /** Show an error message to the user when an incorrect info is entered */
  private void incorrectUser() {
    // Letting the controller to call this function on the UI thread, and apply the changes
    Platform.runLater(
        () ->
            ((DesktopLoginViewController) view_controller)
                .setFailedText("Incorrect ID or password"));
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
      case (CLINIC_MANAGER):
      case (HMO_MANAGER):
        FXMLLoader loader =
            new FXMLLoader(
                getClass().getResource(ClientUtils.get_fxml(ManagerDashboardViewController.class)));
        loader.setControllerFactory(
            c -> {
              return new ManagerDashboardViewController(user);
            });
        ClientUtils.openNewWindow(
            ManagerDashboardViewController.class, ManagerDashboardController.class, loader, true);

        break;

      case (PATIENT):
        loader =
            new FXMLLoader(
                getClass().getResource(ClientUtils.get_fxml(PatientDashboardViewController.class)));

        loader.setControllerFactory(
            c ->
                new PatientDashboardViewController(
                    HMODesktopClient.getClient().getConnected_patient()));

        ClientUtils.openNewWindow(
            PatientDashboardViewController.class, PatientDashboardController.class, loader, false);

        break;

      default:
        throw new NotImplementedException("Invalid role");
    }
  }
}
