package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;

public class PatientQueueAppointmentEvent extends Event {

    public AppointmentType appointment_type;
    public String number_in_line;
    public Patient patient;

    public PatientQueueAppointmentEvent(AppointmentType appointment_type, String number_in_line, Patient patient, Object sender) {
        super(sender);
        this.appointment_type = appointment_type;
        this.number_in_line = number_in_line;
        this.patient = patient;
    }
}
