package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.gui.ResourcePath;

import il.cshaifa.hmo_system.client.gui.login.LoginController;
import il.cshaifa.hmo_system.client.gui.login.LoginViewController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    var loader =
        new FXMLLoader(App.class.getResource(ResourcePath.get_fxml(LoginViewController.class)));
    var scene = new Scene(loader.load());

    var view_controller = (LoginViewController) loader.getController();

    var c = new LoginController(view_controller);
    primaryStage.setScene(scene);
    primaryStage.setTitle("HMO System Login");
    primaryStage.show();
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
