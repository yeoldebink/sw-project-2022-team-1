package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.CommonEnums.SetAppointmentAction;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;

public class SetAppointmentMessage extends Message {

  public SetAppointmentAction action;
  public Patient patient;
  public Appointment appointment;
  public boolean success;

  public SetAppointmentMessage(
      SetAppointmentAction request, Patient patient, Appointment appointment) {
    super(MessageType.REQUEST);
    this.action = request;
    this.patient = patient;
    this.appointment = appointment;
  }
}
