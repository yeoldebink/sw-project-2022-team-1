package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import java.util.List;

public class SetAppointmentEvent extends Event {
  public enum Action {
    LOCK,
    TAKE,
    RELEASE,
    ROLES_RESPONSE,
    AUTHORIZE,
    REJECT
  }

  public Action action;
  public Patient patient;
  public Appointment appointment;
  public AppointmentType appointmentType;
  public Role role;
  public List<Role> specialistRoles;

  public SetAppointmentEvent(
      Object sender, Action action, Patient patient, Appointment appointment) {
    super(sender);
    this.action = action;
    this.patient = patient;
    this.appointment = appointment;
  }

  public SetAppointmentEvent(Object sender, List<Role> role_list){
    super(sender);
    action = Action.ROLES_RESPONSE;
    specialistRoles = role_list;
  }
}
