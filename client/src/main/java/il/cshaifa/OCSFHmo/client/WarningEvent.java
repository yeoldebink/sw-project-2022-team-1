package il.cshaifa.OCSFHmo.client;

public class WarningEvent {

  private final ServerFunctionalities warning;

  public ServerFunctionalities getWarning() {
    return warning;
  }

  public WarningEvent(ServerFunctionalities warning) {
    this.warning = warning;
  }
}
