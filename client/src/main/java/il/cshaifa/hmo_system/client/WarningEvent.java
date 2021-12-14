package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.entities.Warning;

public class WarningEvent {
  private Warning warning;

  public Warning getWarning() {
    return warning;
  }

  public WarningEvent(Warning warning) {
    this.warning = warning;
  }
}
