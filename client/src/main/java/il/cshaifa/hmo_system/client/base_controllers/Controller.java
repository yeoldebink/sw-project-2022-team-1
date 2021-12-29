package il.cshaifa.hmo_system.client.base_controllers;

import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import javafx.stage.Stage;

public abstract class Controller {
  protected ViewController view_controller;
  protected Stage stage;

  public Controller(ViewController view_controller, Stage stage) {
    this.view_controller = view_controller;
    this.stage = stage;
  }

  public abstract void OnWindowCloseEvent(CloseWindowEvent event);
}
