package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;
import java.util.List;

public class ClinicMessage extends Message {
  public List<Clinic> clinics;

  public ClinicMessage() {
    this.message_type = messageType.REQUEST;
    clinics = null;
  }
}
