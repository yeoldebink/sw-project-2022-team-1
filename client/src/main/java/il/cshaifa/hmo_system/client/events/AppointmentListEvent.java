package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.User;

public class AppointmentListEvent {
    public User staff_member;

    public AppointmentListEvent(User staff_member) {
        this.staff_member = staff_member;
    }
}
