package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.User;
import java.util.ArrayList;

public class AdminAppointmentListEvent {
  public User staff_member;
  public ArrayList<Appointment> appointments;

  public enum Phase {
    REQUEST,
    RECEIVE
  }

  public AdminAppointmentListEvent(User staff_member, ArrayList<Appointment> appointments) {
    this.staff_member = staff_member;
    this.appointments = appointments;
  }
}
