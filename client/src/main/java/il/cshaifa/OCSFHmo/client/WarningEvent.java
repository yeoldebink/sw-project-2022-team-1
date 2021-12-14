package il.cshaifa.OCSFHmo.client;

import il.cshaifa.OCSFHmo.entities.Warning;
public class WarningEvent {
  private Warning warning;

  public Warning getWarning() {
    return warning;
  }

  public WarningEvent(Warning warning) {
    this.warning = warning;
  }
}