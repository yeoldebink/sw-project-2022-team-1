package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;

import java.time.LocalDateTime;

public class AddAppointmentEvent {
    public LocalDateTime start_datetime;
    public User staff_member;
    public Phase phase;
    public Integer count_appointments;
    public Clinic clinic;

    public AddAppointmentEvent(User staff_member, Clinic clinic, LocalDateTime start_datetime, Integer count_appointments, Phase phase) {
        this.staff_member = staff_member;
        this.clinic = clinic;
        this.start_datetime = start_datetime;
        this.count_appointments = count_appointments;
        this.phase = phase;

    }

    public enum Phase {
        OPEN_WINDOW,
        SEND
    }
}