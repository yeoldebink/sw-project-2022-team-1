package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;

public class LoginEvent {
  public int id;
  public String password;
  public Phase phase;

  public User userData;
  public Patient patientData;

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
