package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Request;
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
        new FXMLLoader(
            App.class.getResource("/il/cshaifa/hmo_system/client/AdminClinicListView.fxml"));

    var scene = new Scene(loader.load());
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
