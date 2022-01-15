package il.cshaifa.hmo_system.client.base_controllers;

import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;

public abstract class Controller {
  protected ViewController view_controller;
  protected Stage stage;

  public Controller(ViewController view_controller, Stage stage) {
    this.view_controller = view_controller;
    this.stage = stage;
    EventBus.getDefault().register(this);

    if (stage != null) stage.setOnCloseRequest(windowEvent -> onWindowClose());
  }

  public boolean hasViewController() {
    return view_controller != null;
  }

  protected void onWindowClose() {
    EventBus.getDefault().unregister(this);
    this.view_controller = null;
  }
}
