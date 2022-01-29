package il.cshaifa.hmo_system.desktop_client;

import il.cshaifa.hmo_system.client_base.HMOClient;
import il.cshaifa.hmo_system.client_base.utils.ClientUtils;
import il.cshaifa.hmo_system.desktop_client.gui.login.DesktopLoginController;
import il.cshaifa.hmo_system.desktop_client.gui.login.DesktopLoginViewController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class App extends Application {

  private HMOClient client;

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    client = HMODesktopClient.getClient();
    client.openConnection();

    FXMLLoader loader =
        new FXMLLoader(App.class.getResource(ClientUtils.get_fxml(DesktopLoginViewController.class)));
    ClientUtils.openNewWindow(
        DesktopLoginViewController.class, DesktopLoginController.class, loader, false);
  }

  @Override
  public void stop() {
    try {
      client.closeConnection();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }
}
