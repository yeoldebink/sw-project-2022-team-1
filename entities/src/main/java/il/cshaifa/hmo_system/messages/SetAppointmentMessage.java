package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;

public class SetAppointmentMessage extends Message{

  public enum Action {
    LOCK,
    TAKE,
    RELEASE
  }
  public Action action;
  public Patient patient;
  public Appointment appointment;
  public boolean success;


  public SetAppointmentMessage(Action action, Patient patient, Appointment appointment) {
    super(MessageType.REQUEST);
    this.action = action;
    this.patient = patient;
    this.appointment = appointment;
  }
}
