package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.client_base.events.Event;

import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.time.LocalDateTime;
import java.util.List;

public class StaffNextAppointmentEvent extends Event {
    public List<QueuedAppointment> updated_queue;
    public LocalDateTime queue_timestamp;
    public QueuedAppointment q_appt;

    private StaffNextAppointmentEvent(List<QueuedAppointment> updated_queue,
        LocalDateTime queue_timestamp, QueuedAppointment q_appt, Object sender) {
        super(sender);
        this.updated_queue = updated_queue;
        this.queue_timestamp = queue_timestamp;
        this.q_appt = q_appt;
    }

    public static StaffNextAppointmentEvent nextAppointmentRequestEvent(Object sender) {
        return new StaffNextAppointmentEvent(null, null, null, sender);
    }

    public static StaffNextAppointmentEvent nextAppointmentResponseEvent(
        List<QueuedAppointment> updated_queue, LocalDateTime queue_timestamp,
        QueuedAppointment q_appt, Object sender) {
        return new StaffNextAppointmentEvent(updated_queue, queue_timestamp, q_appt, sender);
    }
}
