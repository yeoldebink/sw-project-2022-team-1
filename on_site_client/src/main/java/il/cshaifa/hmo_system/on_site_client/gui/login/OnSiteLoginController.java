package il.cshaifa.hmo_system.on_site_client.gui.login;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.events.ClinicEvent;
import il.cshaifa.hmo_system.client_base.events.LoginEvent;
import il.cshaifa.hmo_system.client_base.events.LoginEvent.Response;
import il.cshaifa.hmo_system.client_base.utils.Utils;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.CloseStationEvent;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteLoginEvent;
import il.cshaifa.hmo_system.on_site_client.gui.patient.OnSitePatientController;
import il.cshaifa.hmo_system.on_site_client.gui.patient.OnSitePatientViewController;
import il.cshaifa.hmo_system.on_site_client.gui.staff.StaffQueueController;
import il.cshaifa.hmo_system.on_site_client.gui.staff.StaffQueueViewController;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class OnSiteLoginController extends Controller {

  private final OnSiteLoginAction action;

  public OnSiteLoginController(ViewController view_controller, Stage stage,
      OnSiteLoginAction action) {
    super(view_controller, stage);
    this.action = action;
    try {
      HMOOnSiteClient.getClient().getClinics();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  /**
   * Event to handle the user request to login to the system
   *
   * @param event Holds the user input to the login screen
   */
  @Subscribe
  public void OnLoginRequestEvent(OnSiteLoginEvent event) {
    if (event.getSender().equals(this.view_controller)) {
      try {
        String pass = event.password.equals("") ? null : event.password;
        HMOOnSiteClient.getClient().loginRequest(event.id, pass, event.clinic);
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
    if (event.getSender().equals(HMOOnSiteClient.getClient())) {
      if (event.response == Response.REJECT) {
        incorrectUser();
      } else if (event.response == Response.AUTHORIZE) {
        if (this.action == OnSiteLoginAction.LOGIN) {
          openMainScreenByRole(event.userData);
        } else {
          Platform.runLater(() -> {
            var dialog = new Dialog<String>();
            dialog.setContentText(String.format("Closed %s successfully", this.action == OnSiteLoginAction.CLOSE_CLINIC ? "clinic" : "station"));
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.show();
            EventBus.getDefault().post(new CloseStationEvent(this));
          });
        }

        Platform.runLater(() -> this.stage.close());
        onWindowClose();
      }
    }
  }

  /** Show an error message to the user when an incorrect info is entered */
  private void incorrectUser() {
    // Letting the controller to call this function on the UI thread, and apply the changes
    Platform.runLater(
        () ->
            ((OnSiteLoginViewController) view_controller)
                .setFailedText("Incorrect/unauthorized login details"));
  }

  /**
   * Handle which main screen we want to show by user role
   *
   * @param user The user that logged in to the system
   * @throws Exception Thrown when opening the screen failed
   */
  private void openMainScreenByRole(User user) throws Exception {
    if (user.getRole().getName().equals("Clinic Manager")) { // open up the patient view
      var loader = new FXMLLoader(getClass().getResource(Utils.get_fxml(OnSitePatientViewController.class)));
      Utils.openNewWindow(OnSitePatientViewController.class, OnSitePatientController.class, loader, false);
    } else {
      var loader = new FXMLLoader(getClass().getResource(Utils.get_fxml(StaffQueueViewController.class)));
      loader.setControllerFactory(c -> new StaffQueueViewController(user));
      Utils.openNewWindow(StaffQueueViewController.class, StaffQueueController.class, loader, true);
    }
  }

  @Subscribe
  public void onClinicEvent(ClinicEvent event) {
    if (!event.getSender().equals(HMOOnSiteClient.getClient())) return;
    Platform.runLater(
        () -> ((OnSiteLoginViewController) this.view_controller).populateClinics(event.receivedClinics)
    );
  }
}
