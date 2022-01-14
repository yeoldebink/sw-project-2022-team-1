package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;

public class LoginEvent extends Event {
  public enum Response {
    AUTHORIZE,
    REJECT
  }

  public int id;
  public String password;
  public User userData;
  public Patient patientData;
  public Response response;

  public LoginEvent(int id, String password, Object sender) {
    super(sender);
    this.id = id;
    this.password = password;
  }
}
