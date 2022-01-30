package il.cshaifa.hmo_system.client_base.events;

import il.cshaifa.hmo_system.entities.Warning;

public class WarningEvent {
  private Warning warning;

  public WarningEvent(Warning warning) {
    this.warning = warning;
  }

  public Warning getWarning() {
    return warning;
  }
}
