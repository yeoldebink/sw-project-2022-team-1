package il.cshaifa.OCSFHmo.client;

import il.cshaifa.OCSFHmo.entities.ServerFunctionalities;

public class WarningEvent {

  private final ServerFunctionalities warning;

  public ServerFunctionalities getWarning() {
    return warning;
  }

  public WarningEvent(ServerFunctionalities warning) {
    this.warning = warning;
  }
}
