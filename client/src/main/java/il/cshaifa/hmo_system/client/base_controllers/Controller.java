package il.cshaifa.hmo_system.client.base_controllers;

import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
