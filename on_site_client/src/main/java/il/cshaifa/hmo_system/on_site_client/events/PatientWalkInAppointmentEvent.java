package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.structs.QueuedAppointment;

public class PatientWalkInAppointmentEvent extends Event {

    public AppointmentType appointment_type;
    public QueuedAppointment q_appt;

    private PatientWalkInAppointmentEvent(AppointmentType appointment_type, QueuedAppointment q_appt, Object sender) {
        super(sender);
        this.appointment_type = appointment_type;
        this.q_appt = q_appt;
    }

    public static PatientWalkInAppointmentEvent newWalkInRequest(AppointmentType appointmentType, Object sender) {
        return new PatientWalkInAppointmentEvent(appointmentType, null, sender);
    }

    public static PatientWalkInAppointmentEvent newWalkInResponse(QueuedAppointment q_appt, Object sender) {
        return new PatientWalkInAppointmentEvent(null, q_appt, sender);
    }
}
