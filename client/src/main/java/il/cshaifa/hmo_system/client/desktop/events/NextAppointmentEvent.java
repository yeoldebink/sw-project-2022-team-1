package il.cshaifa.hmo_system.client.desktop.events;

import il.cshaifa.hmo_system.client.events.Event;
import il.cshaifa.hmo_system.entities.Appointment;

public class NextAppointmentEvent extends Event {
  public Appointment appointment;

  public NextAppointmentEvent(Object sender, Appointment appointment) {
    super(sender);
    this.appointment = appointment;
  }
}
