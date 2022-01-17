package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.CommonEnums.AddAppointmentRejectionReason;
import il.cshaifa.hmo_system.entities.User;
import java.time.LocalDateTime;

public class AddAppointmentEvent extends Event {

  public LocalDateTime start_datetime;
  public User staff_member;
  public Integer count;
  public boolean success;
  public AddAppointmentRejectionReason reject;

  public AddAppointmentEvent(Object sender) {
    super(sender);
  }

  public AddAppointmentEvent(
      User staff_member, LocalDateTime start_datetime, Integer count_appointments, Object sender) {
    super(sender);
    this.staff_member = staff_member;
    this.start_datetime = start_datetime;
    this.count = count_appointments;
  }
}
