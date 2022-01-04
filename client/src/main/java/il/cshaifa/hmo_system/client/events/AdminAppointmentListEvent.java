package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.User;
import java.util.ArrayList;

public class AdminAppointmentListEvent extends AppointmentListEvent {
  public User staff_member;

  public AdminAppointmentListEvent(
      User staff_member, ArrayList<Appointment> appointments, Phase phase) {
    super(appointments, phase);
    this.staff_member = staff_member;
  }
}
