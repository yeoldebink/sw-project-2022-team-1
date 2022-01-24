package il.cshaifa.hmo_system.desktop_client.gui.login;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.events.LoginEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;

public class DesktopLoginViewController extends ViewController {

  @FXML
  public void initialize() {}

  @FXML private TextField idTextField;

  @FXML private PasswordField passwordField;

  @FXML private Label statusLabel;

  @FXML private Button loginButton;

  private void setDisable(boolean f) {
    idTextField.setDisable(f);
    passwordField.setDisable(f);
    loginButton.setDisable(f);
  }

  @FXML
  void requestLogin(ActionEvent event) {
    // this is to prevent extra login attempts before the server responds
    setDisable(true);

    statusLabel.setTextFill(Color.ALICEBLUE);
    statusLabel.setText("Please wait");

    try {
      LoginEvent login_event =
          new LoginEvent(Integer.parseInt(idTextField.getText()), passwordField.getText(), this);
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
}
