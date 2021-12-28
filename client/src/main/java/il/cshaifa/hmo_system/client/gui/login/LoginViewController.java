package il.cshaifa.hmo_system.client.gui.login;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.messageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;

public class LoginViewController extends ViewController {

  @FXML
  public void initialize() {}

  @FXML private TextField idTextField;

  @FXML private PasswordField passwordField;

  @FXML private Label statusLabel;

  private void setEditable(boolean f) {
    idTextField.setEditable(f);
    passwordField.setEditable(f);
  }

  @FXML
  void requestLogin(ActionEvent event) {
    // this is to prevent extra login attempts before the server responds
    setEditable(false);

    statusLabel.setTextFill(Color.ALICEBLUE);
    statusLabel.setText("Please wait");

    try {
      LoginMessage msg =
          new LoginMessage(Integer.parseInt(idTextField.getText()), passwordField.getText());
      msg.message_type = messageType.REQUEST;
      EventBus.getDefault().post(msg);
    } catch (NumberFormatException e) {
      statusLabel.setTextFill(Color.DARKRED);
      statusLabel.setText("ID must be a number");
      setEditable(true);
    }
  }

  @FXML
  void clearStatusText() {
    statusLabel.setText("");
  }

  void setFailedText() {
    statusLabel.setTextFill(Color.DARKRED);
    statusLabel.setText("Incorrect ID or password");

    // re-enable text entry
    setEditable(true);
  }
}
