package il.cshaifa.hmo_system.client.base_controllers;

import il.cshaifa.hmo_system.client.events.CloseWindowEvent;

public abstract class Controller {
  protected ViewController view_controller;

  public Controller(ViewController view_controller) {
    this.view_controller = view_controller;
  }

  public abstract void OnWindowCloseEvent(CloseWindowEvent event);
}
