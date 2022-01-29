package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.structs.QueuedAppointment;

public class ViewAppointmentEvent extends Event {
  public QueuedAppointment q_appt;

  public ViewAppointmentEvent(QueuedAppointment q_appt, Object sender) {
    super(sender);
    this.q_appt = q_appt;
  }
}
