package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;

public class SetAppointmentMessage extends Message {

  public enum RequestType {
    LOCK,
    TAKE,
    RELEASE
  }

  public RequestType request;
  public Patient patient;
  public Appointment appointment;
  public boolean success;

  public SetAppointmentMessage(RequestType request, Patient patient, Appointment appointment) {
    super(MessageType.REQUEST);
    this.request = request;
    this.patient = patient;
    this.appointment = appointment;
  }
}
