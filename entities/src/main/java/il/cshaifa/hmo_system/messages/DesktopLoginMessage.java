package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import java.util.List;

public class DesktopLoginMessage extends LoginMessage {
  public Patient patient_data;
  public List<Clinic> employee_clinics;
  public boolean already_logged_in;

  public DesktopLoginMessage(int id, String password) {
    super(MessageType.REQUEST, id, password);
    this.id = id;
    this.password = password;
    this.patient_data = null;
    this.already_logged_in = false;
  }
}
