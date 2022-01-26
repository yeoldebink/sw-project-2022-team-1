package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;

public class OnSiteEntryMessage extends Message {

  public int id;
  public Clinic clinic;

  public Patient patient;
  public String place_in_line;
  public Appointment appointment;

  public OnSiteEntryMessage(MessageType message_type, int id, Clinic clinic) {
    super(message_type);
    this.id = id;
    this.clinic = clinic;
  }
}
