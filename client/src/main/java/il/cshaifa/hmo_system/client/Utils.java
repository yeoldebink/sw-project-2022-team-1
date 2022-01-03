package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Utils {
  public static void OpenNewWindow(Class<?> view_controller, Class<?> controller, FXMLLoader loader,
      boolean resizeable)
      throws Exception {
    // Letting us run things on the UI Thread, otherwise things won't change and an exception will
    // be thrown.
    Platform.runLater(
        () -> {
          try {
            Stage stage = new Stage();
            stage.setResizable(resizeable);
            var scene = new Scene(loader.load());
            var v_controller = view_controller.cast(loader.getController());
            var ctor = controller.getConstructor(ViewController.class, Stage.class);
            var control = controller.cast(ctor.newInstance(v_controller, stage));
            stage.setScene(scene);
            stage.show();
          } catch (IOException
              | InvocationTargetException
              | NoSuchMethodException
              | InstantiationException
              | IllegalAccessException e) {
            e.printStackTrace();
          }
        });
  }

  public static Pair<Pane, ViewController> loadFXML(Class<?> requestor, Class<?> target)
      throws IOException {
    var loader = new FXMLLoader(requestor.getResource(ResourcePath.get_fxml(target)));
    return new Pair<Pane, ViewController>(loader.load(), loader.getController());
  }
}
