package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.structs.QueuedAppointment;

public class OnSiteEntryEvent extends Event {

  public int id;
  public QueuedAppointment q_appt;
  public Patient patient; // if null, login was invalid

  private OnSiteEntryEvent(int id, Object sender) {
    super(sender);
    this.id = id;
  }

  private OnSiteEntryEvent(QueuedAppointment q_appt, Patient patient, Object sender) {
    super(sender);
    this.q_appt = q_appt;
    this.patient = patient;
  }

  public static OnSiteEntryEvent entryRequestEvent(int id, Object sender) {
    return new OnSiteEntryEvent(id, sender);
  }

  public static OnSiteEntryEvent entryResponseEvent(
      QueuedAppointment q_appt, Patient patient, Object sender) {
    return new OnSiteEntryEvent(q_appt, patient, sender);
  }
}
