package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import java.util.ArrayList;

public class AppointmentListEvent {

  public ArrayList<Appointment> appointments;
  public Object senderInstance;

  public AppointmentListEvent(ArrayList<Appointment> appointments, Object senderInstance) {
    this.appointments = appointments;
    this.senderInstance = senderInstance;
  }
}
