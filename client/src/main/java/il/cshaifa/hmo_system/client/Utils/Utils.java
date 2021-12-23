package il.cshaifa.hmo_system.client.Utils;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class Utils {
    public static void OpenNewWindow(String stageTitle,
                                     Class<?> view_controller,
                                     Class<?> controller,
                                     FXMLLoader loader) throws Exception {
        var scene = new Scene(loader.load());
        var v_controller = view_controller.cast(loader.getController());
        var ctor = controller.getConstructor(ViewController.class);
        var control = controller.cast(ctor.newInstance(v_controller));
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(stageTitle);
        stage.show();
    }
}
