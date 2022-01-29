package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.Appointment;

public class ViewAppointmentEvent extends Event {
  public Appointment appointment;

  public ViewAppointmentEvent(Appointment appointment, Object sender) {
    super(sender);
    this.appointment = appointment;
  }
}
