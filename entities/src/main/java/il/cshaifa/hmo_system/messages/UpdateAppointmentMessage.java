package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;

public class UpdateAppointmentMessage extends Message {

  public Appointment appointment;

  public UpdateAppointmentMessage(Appointment appointment) {
    super(MessageType.REQUEST);
    this.appointment = appointment;
  }
}
