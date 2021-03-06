package il.cshaifa.hmo_system.desktop_client.events;

import il.cshaifa.hmo_system.client_base.events.AppointmentListEvent;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.User;
import java.util.ArrayList;

public class AdminAppointmentListEvent extends AppointmentListEvent {
  public User staff_member;

  public AdminAppointmentListEvent(
      User staff_member, ArrayList<Appointment> appointments, Object sender) {
    super(appointments, sender);
    this.staff_member = staff_member;
  }
}
