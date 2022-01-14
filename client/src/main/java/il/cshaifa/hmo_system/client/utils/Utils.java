package il.cshaifa.hmo_system.client.utils;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Utils<C> {
  public static void openNewWindow(
      Class<?> view_controller, Class<?> controller, FXMLLoader loader, boolean resizeable)
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

  public static void openNewSingletonWindow(
      Class<?> view_controller,
      Class<?> controller,
      boolean resizeable,
      Callback<Class<?>, Object> ctrl_factory) {
    Platform.runLater(
        () -> {
          try {
            // check if an instance already exists
            var getInstance = controller.getMethod("getInstance");
            var instance = getInstance.invoke(null);
            if (instance != null && ((Controller) instance).hasViewController()) return;

            // load
            var loader =
                new FXMLLoader(controller.getResource(ResourcePath.get_fxml(view_controller)));
            if (ctrl_factory != null) loader.setControllerFactory(ctrl_factory);

            Stage stage = new Stage();
            stage.setResizable(resizeable);

            // create instance
            var create = controller.getMethod("create", ViewController.class, Stage.class);
            create.invoke(null, loader.getController(), stage);

            var scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();

          } catch (NoSuchMethodException
              | InvocationTargetException
              | IllegalAccessException
              | IOException e) {
            e.printStackTrace();
          }
        });
  }

  public static LoadedPane loadFXML(
      Class<?> requestor, Class<?> target, Callback<Class<?>, Object> ctrl_factory)
      throws IOException {
    var loader = new FXMLLoader(requestor.getResource(ResourcePath.get_fxml(target)));

    if (ctrl_factory != null) {
      loader.setControllerFactory(ctrl_factory);
    }

    return new LoadedPane(loader.load(), loader.getController());
  }

  public static LoadedPane loadFXML(Class<?> requestor, Class<?> target) throws IOException {
    return loadFXML(requestor, target, null);
  }
}
