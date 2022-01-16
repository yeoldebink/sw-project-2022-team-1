package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage.Action;

public class SetAppointmentEvent extends Event {

  public enum Response {
    AUTHORIZE,
    REJECT
  }

  public Action action;
  public Response response;
  public Patient patient;
  public Appointment appointment;
  public AppointmentType appointmentType;
  public Role role;

  public SetAppointmentEvent(
      Object sender, Action action, Patient patient, Appointment appointment) {
    super(sender);
    this.action = action;
    this.patient = patient;
    this.appointment = appointment;
  }
}
