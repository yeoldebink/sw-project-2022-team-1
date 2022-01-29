package il.cshaifa.hmo_system.on_site_client;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.client_base.HMOClient;
import il.cshaifa.hmo_system.client_base.utils.ClientUtils;
import il.cshaifa.hmo_system.on_site_client.gui.login.OnSiteLoginController;
import il.cshaifa.hmo_system.on_site_client.gui.login.OnSiteLoginViewController;
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
    client = HMOOnSiteClient.getClient();
    client.openConnection();

    FXMLLoader loader =
        new FXMLLoader(App.class.getResource(ClientUtils.get_fxml(OnSiteLoginViewController.class)));

    Scene scene = new Scene(loader.load());
    Stage stage = new Stage();
    stage.setScene(scene);

    OnSiteLoginController c = new OnSiteLoginController(loader.getController(), stage, OnSiteLoginAction.LOGIN);
    stage.show();
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
