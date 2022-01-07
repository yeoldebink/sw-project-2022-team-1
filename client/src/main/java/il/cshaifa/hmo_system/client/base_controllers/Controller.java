package il.cshaifa.hmo_system.client.base_controllers;

import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public abstract class Controller {
  protected ViewController view_controller;
  protected Stage stage;

  public Controller(ViewController view_controller, Stage stage) {
    this.view_controller = view_controller;
    this.stage = stage;
    EventBus.getDefault().register(this);
  }

  @Subscribe
  public void onWindowCloseEvent(CloseWindowEvent event){
    if (event.getViewControllerInstance().equals(this.view_controller))
      EventBus.getDefault().unregister(this);
  }
}
