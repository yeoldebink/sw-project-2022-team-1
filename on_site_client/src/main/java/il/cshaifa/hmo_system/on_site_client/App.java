package il.cshaifa.hmo_system.on_site_client;

import il.cshaifa.hmo_system.client_base.HMOClient;
import il.cshaifa.hmo_system.client_base.utils.Utils;
import il.cshaifa.hmo_system.on_site_client.gui.login.OnSiteLoginController;
import il.cshaifa.hmo_system.on_site_client.gui.login.OnSiteLoginViewController;
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
    client = HMOOnSiteClient.getClient();
    client.openConnection();

    FXMLLoader loader =
        new FXMLLoader(App.class.getResource(Utils.get_fxml(OnSiteLoginViewController.class)));
    Utils.openNewWindow(
        OnSiteLoginViewController.class, OnSiteLoginController.class, loader, false);
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
