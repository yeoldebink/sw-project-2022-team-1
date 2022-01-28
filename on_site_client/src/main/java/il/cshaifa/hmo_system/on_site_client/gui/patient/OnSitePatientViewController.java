package il.cshaifa.hmo_system.on_site_client.gui.patient;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteEntryEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;

import static java.lang.Thread.sleep;

public class OnSitePatientViewController extends ViewController {

  @FXML private MenuItem closeStationMenuItem; // TODO implement me!
  @FXML private MenuItem closeClinicMenuItem; // TODO implement me!

  @FXML private Label clinicWelcomeLabel;
  @FXML private TextField idTextField;

  @FXML private Button goButton;

  @FXML private Label errorLabel;

  public OnSitePatientViewController() {}

  @FXML public void initialize() {
    errorLabel.setTextFill(Color.DARKRED);

    clinicWelcomeLabel.setText(String.format("Welcome to %s Clinic", HMOOnSiteClient.getClient().getStationClinic()));

    idTextField.textProperty().addListener((obs, oldval, newval) -> errorLabel.setVisible(false));

    goButton.setOnAction((event) -> {
      try {
        int id = Integer.parseInt(idTextField.getText());
        EventBus.getDefault().post(OnSiteEntryEvent.entryRequestEvent(id, this));
      } catch (NumberFormatException e) {
        invalidID();
      }
    });
  }

  private void setError(String error) {
    errorLabel.setText(error);
    errorLabel.setVisible(true);
  }

  public void invalidID() {
    setError("Invalid card or ID entered - please try again.");
  }

  public void notInClinic(Clinic clinic) {
    setError(String.format("You have no appointments in this clinic.\n"
        + "For walk-in appointments please visit your home clinic (%s).", clinic.getName()));
  }

  public void showDashboard(Patient patient) {} // TODO implement me!

  public void returnToEntryScreen() {} // TODO implement me!

  public void showExitContextMenu(ContextMenuEvent contextMenuEvent) {
    ContextMenu cm = new ContextMenu();
    cm.getItems().add(new MenuItem("Hello!"));
  }
}
