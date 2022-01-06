package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.login.LoginController;
import il.cshaifa.hmo_system.client.gui.login.LoginViewController;
import il.cshaifa.hmo_system.client.utils.Utils;
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
    client = HMOClient.getClient();
    client.openConnection();

    FXMLLoader loader =
        new FXMLLoader(App.class.getResource(ResourcePath.get_fxml(LoginViewController.class)));
    Utils.OpenNewWindow(LoginViewController.class, LoginController.class, loader, false);
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
