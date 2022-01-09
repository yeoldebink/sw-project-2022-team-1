package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.AdminAppointmentMessageType;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.RejectionType;
import java.time.LocalDateTime;

public class AddAppointmentEvent {
  public LocalDateTime start_datetime;
  public User staff_member;
  public Phase phase;
  public Integer count;
  public AdminAppointmentMessageType response_type;
  public RejectionType rejectionType;

  public AddAppointmentEvent(
      User staff_member, LocalDateTime start_datetime, Integer count_appointments, Phase phase) {
    this.staff_member = staff_member;
    this.start_datetime = start_datetime;
    this.count = count_appointments;
    this.phase = phase;
  }

  public enum Phase {
    OPEN_WINDOW,
    SEND,
    RECEIVE
  }
}
