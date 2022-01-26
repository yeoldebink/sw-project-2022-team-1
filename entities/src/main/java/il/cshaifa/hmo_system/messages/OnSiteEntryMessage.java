package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;

public class OnSiteEntryMessage extends Message {

  public int id;
  public Clinic clinic;

  public Patient patient;
  public boolean belongs_to_clinic;
  public String place_in_line;

  public OnSiteEntryMessage(MessageType message_type, int id, Clinic clinic) {
    super(message_type);
    this.id = id;
    this.clinic = clinic;
  }
}
