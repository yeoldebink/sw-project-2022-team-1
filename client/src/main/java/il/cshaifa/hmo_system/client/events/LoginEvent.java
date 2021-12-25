package il.cshaifa.hmo_system.client.events;

public class LoginEvent {
  public int id;
  public String password;
  public Phase phase;

  public enum Phase {
    SEND,
    AUTHORIZE,
    REJECT
  }

  public LoginEvent(int id, String password) {
    this.id = id;
    this.password = password;
    this.phase = Phase.SEND;
  }
}
