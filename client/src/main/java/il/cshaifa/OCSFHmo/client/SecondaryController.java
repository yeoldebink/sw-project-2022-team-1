package il.ac.haifa.client_server.client.src.main.java.il.cshaifa.OCSFHmo.client;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

  @FXML
  private void switchToPrimary() throws IOException {
    App.setRoot("primary");
  }
}
