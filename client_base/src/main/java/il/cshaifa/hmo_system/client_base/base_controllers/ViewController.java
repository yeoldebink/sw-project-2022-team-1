package il.cshaifa.hmo_system.client_base.base_controllers;

import il.cshaifa.hmo_system.client_base.events.CloseWindowEvent;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;

public class ViewController {
  protected void closeWindow(ActionEvent event) {
    Node source = (Node) event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    EventBus.getDefault().post(new CloseWindowEvent(this));
    stage.close();
  }
}
