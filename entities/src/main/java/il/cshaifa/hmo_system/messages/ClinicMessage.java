package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;
import java.util.Collections;
import java.util.List;

public class ClinicMessage extends Message {
  public List<Clinic> clinics;

  public ClinicMessage() {
    super(messageType.REQUEST);
    clinics = null;
  }

  public ClinicMessage(Clinic edited_clinic) {
    super(messageType.REQUEST);
    clinics = Collections.singletonList(edited_clinic);
  }
}
