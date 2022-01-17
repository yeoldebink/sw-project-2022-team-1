package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import java.util.List;

public class SetAppointmentEvent extends Event {
  public enum RequestType {
    LOCK,
    TAKE,
    RELEASE
  }

  public enum ResponseType {
    ROLES_RESPONSE,
    AUTHORIZE,
    REJECT
  }

  public RequestType request;
  public ResponseType response;
  public Patient patient;
  public Appointment appointment;
  public AppointmentType appointmentType;
  public Role role;
  public List<Role> specialistRoles;

  public SetAppointmentEvent(
      Object sender, RequestType request, Patient patient, Appointment appointment) {
    super(sender);
    this.request = request;
    this.patient = patient;
    this.appointment = appointment;
  }

  public SetAppointmentEvent(Object sender, List<Role> role_list) {
    super(sender);
    response = ResponseType.ROLES_RESPONSE;
    specialistRoles = role_list;
  }

  public SetAppointmentEvent(
      Object sender, Patient patient, Appointment appointment) {
    super(sender);
    this.patient = patient;
    this.appointment = appointment;
  }
}
