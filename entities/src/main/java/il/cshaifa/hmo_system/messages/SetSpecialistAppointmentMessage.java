package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import java.util.List;

public class SetSpecialistAppointmentMessage extends Message{

  public enum RequestType {
    GET_ROLES,
    GET_APPOINTMENTS
  }

  public Patient patient;
  public RequestType request;
  public Role chosen_role;
  public List<Role> role_list;
  public List<Appointment> appointments;

  public SetSpecialistAppointmentMessage() {
    super(MessageType.REQUEST);
    this.request = RequestType.GET_ROLES;
  }

  public SetSpecialistAppointmentMessage(Role role, Patient patient) {
    super(MessageType.REQUEST);
    this.request = RequestType.GET_APPOINTMENTS;
    this.chosen_role = role;
    this.patient = patient;
  }
}
