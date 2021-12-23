package il.cshaifa.hmo_system.client.base_controllers;

import il.cshaifa.hmo_system.client.events.CloseWindowEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public abstract class Controller {
  protected ViewController view_controller;

  public Controller(ViewController view_controller) {
    this.view_controller = view_controller;
  }

  @Subscribe
  protected abstract void OnWindowCloseEvent(CloseWindowEvent event);
}
