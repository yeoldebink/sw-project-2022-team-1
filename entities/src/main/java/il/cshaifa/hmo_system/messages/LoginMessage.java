package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import java.util.List;

public class LoginMessage extends Message {
  /* Request fields */
  public int id;
  public String password;
  /* Respond fields */
  public User user;
  public Patient patient_data;
  public List<Clinic> employee_clinics;

  public LoginMessage(int id, String password) {
    super(messageType.REQUEST);
    this.id = id;
    this.password = password;
    this.user = null;
    this.patient_data = null;
  }
}
