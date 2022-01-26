package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;

public class PatientEntryEvent extends Event {
    public enum Response {
        AUTHORIZE,
        REJECT,
        LOGGED_IN
    }

    public int id;
    public Appointment appointment;
    public String number_in_line;
    public Patient patient;
    public Response response;

    public PatientEntryEvent(int id, Object sender) {
        super(sender);
        this.id = id;
    }

    public PatientEntryEvent(Appointment appointment, String number_in_line, Patient patient, Object sender) {
        super(sender);
        this.appointment = appointment;
        this.number_in_line = number_in_line;
        this.patient = patient;
    }
}
