package il.cshaifa.hmo_system.on_site_client.gui.login;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteLoginEvent;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;

public class OnSiteLoginViewController extends ViewController {

  @FXML private ComboBox<Clinic> clinicComboBox;

  @FXML
  public void initialize() {
    clinicComboBox
        .valueProperty()
        .addListener(
            (obs, oldval, newval) -> {
              if (newval != null) {
                loginButton.setDisable(false);
              }
            });
  }

  @FXML private TextField idTextField;

  @FXML private PasswordField passwordField;

  @FXML private Label statusLabel;

  @FXML private Button loginButton;

  private void setDisable(boolean f) {
    idTextField.setDisable(f);
    passwordField.setDisable(f);
    loginButton.setDisable(f);
    clinicComboBox.setDisable(f);
  }

  @FXML
  void requestLogin(ActionEvent event) {
    if (this.loginButton.isDisable()) return;
    // this is to prevent extra login attempts before the server responds
    setDisable(true);

    statusLabel.setTextFill(Color.ALICEBLUE);
    statusLabel.setText("Please wait");

    try {
      var login_event =
          new OnSiteLoginEvent(
              Integer.parseInt(idTextField.getText()),
              passwordField.getText(),
              clinicComboBox.getValue(),
              this);
      EventBus.getDefault().post(login_event);
    } catch (NumberFormatException e) {
      statusLabel.setTextFill(Color.DARKRED);
      statusLabel.setText("ID must be a number");
      setDisable(false);
    }
  }

  @FXML
  void clearStatusText() {
    statusLabel.setText("");
  }

  void setFailedText(String failedText) {
    statusLabel.setTextFill(Color.DARKRED);
    statusLabel.setText(failedText);

    // re-enable changes
    setDisable(false);
  }

  /**
   * Populates view clinic combo box
   * @param clinics Clinics to populate the combo box
   */
  public void populateClinics(List<Clinic> clinics) {
    clinicComboBox.getItems().setAll(clinics);
  }
}
