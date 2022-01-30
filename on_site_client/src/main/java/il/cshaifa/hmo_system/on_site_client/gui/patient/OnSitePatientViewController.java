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
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;

public class OnSitePatientViewController extends ViewController {

  private PauseTransition inactivity_timer;

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

  @FXML private Label dashboardErrorLabel;

  public OnSitePatientViewController() {}

  @FXML
  public void onMouseClicked(MouseEvent event) {
    if (stackPane.getChildren().get(1).isVisible()) {
      inactivity_timer.playFromStart();
    }
  }

  @FXML
  public void initialize() {
    inactivity_timer = new PauseTransition(Duration.seconds(30));
    inactivity_timer.setOnFinished(actionEvent -> returnToEntryScreen());

    stackPane.getChildren().get(1).setVisible(false);

    errorLabel.setTextFill(Color.DARKRED);

    clinicWelcomeLabel.setText(
        String.format("Welcome to %s Clinic", HMOOnSiteClient.getClient().getStationClinic()));

    idTextField.textProperty().addListener((obs, oldval, newval) -> errorLabel.setVisible(false));

    goButton.setOnAction(
        (event) -> {
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

    closeStationMenuItem.setOnAction(
        (actionEvent) -> postExitEvent(OnSiteLoginAction.CLOSE_STATION));

    closeClinicMenuItem.setOnAction((actionEvent) -> postExitEvent(OnSiteLoginAction.CLOSE_CLINIC));

    nurseButton.setOnAction(actionEvent -> postWalkInEvent("Nurse"));

    labButton.setOnAction(actionEvent -> postWalkInEvent("Lab Tests"));

    dashboardWelcomeLabel.setTextFill(Color.web("#4eb5d5"));

    dashboardErrorLabel.setTextFill(Color.DARKRED);
  }

  private void postWalkInEvent(String appt_type) {
    EventBus.getDefault()
        .post(PatientWalkInAppointmentEvent.newWalkInRequest(new AppointmentType(appt_type), this));
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
    setError(
        String.format(
            """
        You have no appointments in this clinic.
        For walk-in appointments please visit
        your home clinic (%s).""",
            clinic.getName()));
  }

  public void showDashboard(Patient patient) {
    dashboardErrorLabel.setVisible(false);
    dashboardWelcomeLabel.setText(String.format("Welcome, %s", patient.getUser()));

    stackPane.getChildren().get(0).setVisible(false);
    stackPane.getChildren().get(1).setVisible(true);

    inactivity_timer.playFromStart();
  }

  public void returnToEntryScreen() {
    errorLabel.setVisible(false);
    stackPane.getChildren().get(0).setVisible(true);
    stackPane.getChildren().get(1).setVisible(false);

    inactivity_timer.stop();
  }

  public void outOfHours() {
    dashboardErrorLabel.setVisible(true);
    dashboardErrorLabel.setText(
        """
        Lab services are available between
        8:00 and 10:00 A.M.""");
  }

  public void alreadyInQueue() {
    dashboardErrorLabel.setVisible(true);
    dashboardErrorLabel.setText(
        """
        You already have a walk-in appointment
        for this service today.""");
  }
}
