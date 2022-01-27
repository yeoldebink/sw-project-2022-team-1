package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;

public class PatientEntryEvent extends Event {

    public int id;
    public Appointment appointment;
    public String number_in_line;
    public Patient patient; // if null, login was invalid

    private PatientEntryEvent(int id, Object sender) {
        super(sender);
        this.id = id;
    }

    private PatientEntryEvent(Appointment appointment, String number_in_line, Patient patient, Object sender) {
        super(sender);
        this.appointment = appointment;
        this.number_in_line = number_in_line;
        this.patient = patient;
    }

    public static PatientEntryEvent entryRequestEvent(int id, Object sender) {
        return new PatientEntryEvent(id, sender);
    }

    public static PatientEntryEvent entryResponseEvent(Appointment appointment, String number_in_line, Patient patient, Object sender) {
        return new PatientEntryEvent(appointment, number_in_line, patient, sender);
    }
}
