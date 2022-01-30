package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.structs.QueuedAppointment;

public class OnSiteEntryMessage extends Message {

  public int id;

  public Patient patient;
  public QueuedAppointment q_appt;

  public OnSiteEntryMessage(int id) {
    super(MessageType.REQUEST);
    this.id = id;
  }
}
