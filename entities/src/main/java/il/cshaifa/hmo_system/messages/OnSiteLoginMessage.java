package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;

public class OnSiteLoginMessage extends LoginMessage {
  public enum Action {
    LOGIN,
    CLOSE_STATION,
    CLOSE_CLINIC
  }

  // request
  public Clinic clinic;
  public Action action;

  public OnSiteLoginMessage(int id, String password,
      Clinic clinic, Action action) {
    super(MessageType.REQUEST, id, password);
    this.clinic = clinic;
    this.action = action;
  }
}
