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

// import entities.src.main.java.il.cshaifa.OCSFHmo.entities.Warning;
//	private Warning warning;
//
//	public Warning getWarning() {
//		return warning;
//	}
//
//	public WarningEvent(Warning warning) {
//		this.warning = warning;
//	}
