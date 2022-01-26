package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.Appointment;

public class PatientOnSiteEntryEvent extends Event {
    public enum Response {
        AUTHORIZE,
        REJECT,
        LOGGED_IN
    }

    public int id;
    public Appointment appointment;
    public String appointment_number;
    public Response response;

    public PatientOnSiteEntryEvent(int id, Object sender) {
        super(sender);
        this.id = id;
    }

    public PatientOnSiteEntryEvent(Appointment appointment, String appointment_number, Object sender) {
        super(sender);
        this.appointment = appointment;
        this.appointment_number = appointment_number;
    }
}
