package il.cshaifa.hmo_system.client.utils;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import javafx.scene.layout.Pane;

public class LoadedPane {

  public final Pane pane;
  public final ViewController view_controller;

  public LoadedPane(Pane pane, ViewController view_controller) {
    this.pane = pane;
    this.view_controller = view_controller;
  }
}
