package il.cshaifa.hmo_system.on_site_client.gui.patient;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteEntryEvent;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteLoginEvent;
import il.cshaifa.hmo_system.on_site_client.events.PatientWalkInAppointmentEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;

import static java.lang.Thread.sleep;

public class OnSitePatientViewController extends ViewController {

  @FXML private StackPane stackPane;

  @FXML private MenuItem closeStationMenuItem;
  @FXML private MenuItem closeClinicMenuItem;

  @FXML private Label clinicWelcomeLabel;
  @FXML private TextField idTextField;

  @FXML private Button goButton;

  @FXML private Label errorLabel;

  @FXML private Label dashboardWelcomeLabel;

  @FXML private Button nurseButton;
  @FXML private Button labButton;

  public OnSitePatientViewController() {}

  @FXML public void initialize() {
    stackPane.getChildren().get(1).setVisible(false);

    errorLabel.setTextFill(Color.DARKRED);

    clinicWelcomeLabel.setText(String.format("Welcome to %s Clinic", HMOOnSiteClient.getClient().getStationClinic()));

    idTextField.textProperty().addListener((obs, oldval, newval) -> errorLabel.setVisible(false));

    goButton.setOnAction((event) -> {
      try {
        String txt = idTextField.getText();
        idTextField.clear();
        int id = Integer.parseInt(txt);
        EventBus.getDefault().post(OnSiteEntryEvent.entryRequestEvent(id, this));
      } catch (NumberFormatException e) {
        invalidID();
      }
    });

    idTextField.setOnAction(goButton.getOnAction());

    closeStationMenuItem.setOnAction((actionEvent) -> postExitEvent(OnSiteLoginAction.CLOSE_STATION));

    closeClinicMenuItem.setOnAction((actionEvent) -> postExitEvent(OnSiteLoginAction.CLOSE_CLINIC));

    nurseButton.setOnAction(actionEvent -> postWalkInEvent("Nurse"));

    labButton.setOnAction(actionEvent -> postWalkInEvent("Lab Tests"));

    dashboardWelcomeLabel.setTextFill(Color.web("#4eb5d5"));
  }

  private void postWalkInEvent(String appt_type) {
    EventBus.getDefault().post(PatientWalkInAppointmentEvent.newWalkInRequest(new AppointmentType(appt_type), this));
  }

  private void postExitEvent(OnSiteLoginAction action) {
    OnSiteLoginEvent event = new OnSiteLoginEvent(0, null, null, this);
    event.action = action;
    EventBus.getDefault().post(event);
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
        + "For walk-in appointments please visit\nyour home clinic (%s).", clinic.getName()));
  }

  public void showDashboard(Patient patient) {
    dashboardWelcomeLabel.setText(String.format("Welcome, %s", patient.getUser()));

    stackPane.getChildren().get(0).setVisible(false);
    stackPane.getChildren().get(1).setVisible(true);
  }

  public void returnToEntryScreen() {
    stackPane.getChildren().get(0).setVisible(true);
    stackPane.getChildren().get(1).setVisible(false);
  }
}
