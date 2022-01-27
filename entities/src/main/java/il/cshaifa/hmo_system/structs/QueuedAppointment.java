package il.cshaifa.hmo_system.structs;

import il.cshaifa.hmo_system.entities.Appointment;

public class QueuedAppointment {
  public final Appointment appointment;
  public final String place;

  public QueuedAppointment(Appointment appointment, String place) {
    this.appointment = appointment;
    this.place = place;
  }
}
