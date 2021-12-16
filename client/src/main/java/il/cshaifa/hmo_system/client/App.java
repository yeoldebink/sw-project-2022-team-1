package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.client.gui.clinic_administration.AdminClinicListController;
import il.cshaifa.hmo_system.client.gui.clinic_administration.AdminClinicListViewController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

  private HMOClient client;

  public final String FXML_BASE_DIR = "";

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    client = HMOClient.getClient();
    client.openConnection();

    var loader =
        new FXMLLoader(
            App.class.getResource(ResourcePath.get_fxml(AdminClinicListViewController.class)));
    var scene = new Scene(loader.load());

    var view_controller = (AdminClinicListViewController) loader.getController();

    var c = new AdminClinicListController(view_controller);
    primaryStage.setScene(scene);
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
