package il.cshaifa.hmo_system.client_base.utils;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ClientUtils {
  /**
   * Opens view component
   *
   * @param view_controller Associated view controller
   * @param controller Associated controller
   * @param loader Loader used to generate scene
   * @param resizeable Should window be resizeable
   * @throws Exception
   */
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

  /**
   * Opens view component, only if not already opened
   *
   * @param view_controller Associated view controller
   * @param controller Associated controller
   * @param resizeable Should window be resizeable
   * @param ctrl_factory Callback
   */
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
                new FXMLLoader(controller.getResource(ClientUtils.get_fxml(view_controller)));
            if (ctrl_factory != null) loader.setControllerFactory(ctrl_factory);

            Stage stage = new Stage();
            stage.setResizable(resizeable);

            var scene = new Scene(loader.load());

            // create instance
            var create = controller.getMethod("create", ViewController.class, Stage.class);
            create.invoke(null, loader.getController(), stage);

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

  /**
   * Loads FXML
   *
   * @param requestor Object requesting fxml to be loaded
   * @param target Target view controller referencing FXML
   * @param ctrl_factory Callback
   * @return returns Loaded Pane containing Pane loaded and view controller
   * @throws IOException
   */
  public static LoadedPane loadFXML(
      Class<?> requestor, Class<?> target, Callback<Class<?>, Object> ctrl_factory)
      throws IOException {
    var loader = new FXMLLoader(requestor.getResource(ClientUtils.get_fxml(target)));

    if (ctrl_factory != null) {
      loader.setControllerFactory(ctrl_factory);
    }

    return new LoadedPane(loader.load(), loader.getController());
  }

  /**
   * Loads FXML
   *
   * @param requestor Object requesting fxml to be loaded
   * @param target Target view controller referencing FXML
   * @return
   * @throws IOException
   */
  public static LoadedPane loadFXML(Class<?> requestor, Class<?> target) throws IOException {
    return loadFXML(requestor, target, null);
  }

  /**
   * Creates fxml path and returns
   *
   * @param view_controller_class FXML's associated view controller
   * @return The fxml path
   */
  public static String get_fxml(Class<?> view_controller_class) {
    String canonicalName = view_controller_class.getCanonicalName();
    String path = "/" + canonicalName.replace(".", "/").replace("Controller", "");
    return path + ".fxml";
  }
}
