package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;

public class SetAppointmentEvent extends Event {
  public enum Action {
    LOCK,
    TAKE,
    RELEASE,
    AUTHORIZE,
    REJECT
  }

  public Action action;
  public Patient patient;
  public Appointment appointment;

  public SetAppointmentEvent(Object sender,
      Action action, Patient patient, Appointment appointment) {
    super(sender);
    this.action = action;
    this.patient = patient;
    this.appointment = appointment;
  }
}
