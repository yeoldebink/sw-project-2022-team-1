package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.structs.QueuedAppointment;

public class PatientEntryEvent extends Event {

    public int id;
    public QueuedAppointment q_appt;
    public Patient patient; // if null, login was invalid

    private PatientEntryEvent(int id, Object sender) {
        super(sender);
        this.id = id;
    }

    private PatientEntryEvent(QueuedAppointment q_appt, Patient patient, Object sender) {
        super(sender);
        this.q_appt = q_appt;
        this.patient = patient;
    }

    public static PatientEntryEvent entryRequestEvent(int id, Object sender) {
        return new PatientEntryEvent(id, sender);
    }

    public static PatientEntryEvent entryResponseEvent(QueuedAppointment q_appt, Patient patient, Object sender) {
        return new PatientEntryEvent(q_appt, patient, sender);
    }
}
