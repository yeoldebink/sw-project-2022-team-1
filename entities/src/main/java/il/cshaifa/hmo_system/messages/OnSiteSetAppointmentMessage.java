package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;

public class OnSiteSetAppointmentMessage extends Message{

  public AppointmentType type;
  public Clinic clinic;
  public Patient patient;

  public int place_in_line;

  public OnSiteSetAppointmentMessage(AppointmentType type, Clinic clinic, Patient patient) {
    super(MessageType.REQUEST);
    this.type = type;
    this.clinic = clinic;
    this.patient = patient;
  }
}
