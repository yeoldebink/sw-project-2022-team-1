package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;

public class LoginEvent {
  public enum Status {
    AUTHORIZE,
    REJECT
  }

  public int id;
  public String password;
  public User userData;
  public Patient patientData;
  public Object senderInstance;
  public Status status;


  public LoginEvent(int id, String password, Object senderInstance) {
    this.id = id;
    this.password = password;
    this.senderInstance = senderInstance;
  }
}
