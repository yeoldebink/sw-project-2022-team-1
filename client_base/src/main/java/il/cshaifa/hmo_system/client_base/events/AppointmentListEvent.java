package il.cshaifa.hmo_system.client_base.events;

import il.cshaifa.hmo_system.entities.Appointment;
import java.util.ArrayList;

public class AppointmentListEvent extends Event {

  public ArrayList<Appointment> appointments;

  public AppointmentListEvent(ArrayList<Appointment> appointments, Object sender) {
    super(sender);
    this.appointments = appointments;
  }
}
