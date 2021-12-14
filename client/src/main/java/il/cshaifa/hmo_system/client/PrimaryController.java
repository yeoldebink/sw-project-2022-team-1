package il.cshaifa.hmo_system.client;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

  @FXML
  void sendWarning(ActionEvent event) {
    try {
      HMOClient.getClient().sendToServer("#warning");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
