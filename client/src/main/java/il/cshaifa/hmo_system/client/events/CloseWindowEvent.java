package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;

public class CloseWindowEvent {
  private ViewController view_controller;

  public CloseWindowEvent(ViewController view_controller) {
    this.view_controller = view_controller;
  }

  public ViewController getViewControllerInstance() {
    return view_controller;
  }
}