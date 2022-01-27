package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;

import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.util.ArrayList;
import java.util.List;

public class StaffNextAppointmentEvent extends Event {
    public List<QueuedAppointment> updated_queue;
    public QueuedAppointment q_appt;

    private StaffNextAppointmentEvent(List<QueuedAppointment> updated_queue, QueuedAppointment q_appt, Object sender) {
        super(sender);
        this.updated_queue = updated_queue;
    }

    public static StaffNextAppointmentEvent nextAppointmentRequestEvent(Object sender) {
        return new StaffNextAppointmentEvent(null, null, sender);
    }

    public static StaffNextAppointmentEvent nextAppointmentResponseEvent(List<QueuedAppointment> updated_queue, QueuedAppointment q_appt, Object sender) {
        return new StaffNextAppointmentEvent(updated_queue, q_appt, sender);
    }
}
