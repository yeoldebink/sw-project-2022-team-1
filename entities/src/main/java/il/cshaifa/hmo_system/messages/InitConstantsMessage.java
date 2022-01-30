package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Role;
import java.util.List;

public class InitConstantsMessage extends Message {
  public List<AppointmentType> appointment_types;
  public List<Role> roles;

  public InitConstantsMessage(List<AppointmentType> appointment_types, List<Role> roles) {
    super(MessageType.RESPONSE);
    this.appointment_types = appointment_types;
    this.roles = roles;
  }
}
