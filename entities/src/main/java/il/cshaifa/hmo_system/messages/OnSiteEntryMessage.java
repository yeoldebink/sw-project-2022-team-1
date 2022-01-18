package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;

public class OnSiteEntryMessage extends Message{

  public int id;
  public String password;
  public Clinic clinic;

  public Patient patient;
  public boolean belongs_to_clinic;
  public Appointment next_appointment;
  public int place_in_line;

  public OnSiteEntryMessage(MessageType message_type, int id, String password,
      Clinic clinic) {
    super(message_type);
    this.id = id;
    this.password = password;
    this.clinic = clinic;
  }
}
