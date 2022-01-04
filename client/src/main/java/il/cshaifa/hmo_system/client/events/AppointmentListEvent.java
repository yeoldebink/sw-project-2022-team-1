package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import java.util.ArrayList;

public class AppointmentListEvent {
  public enum Phase {
    OPEN_WINDOW,
    REQUEST,
    RECEIVE
  }

  public ArrayList<Appointment> appointments;
  public Phase phase;

  public AppointmentListEvent(ArrayList<Appointment> appointments, Phase phase) {
    this.appointments = appointments;
    this.phase = phase;
  }
}
