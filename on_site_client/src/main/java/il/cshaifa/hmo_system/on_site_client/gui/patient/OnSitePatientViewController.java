package il.cshaifa.hmo_system.on_site_client.gui.patient;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class OnSitePatientViewController extends ViewController {

  @FXML private MenuItem closeStationMenuItem;
  @FXML private MenuItem closeClinicMenuItem;
  @FXML private Label clinicWelcomeLabel;
  @FXML private TextField idTextField;

  public OnSitePatientViewController() {}

  @FXML public void initialize() {
    clinicWelcomeLabel.setText(String.format("Welcome to %s Clinic", HMOOnSiteClient.getClient().getConnected_employee_clinics().get(0)));
  }
}
