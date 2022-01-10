package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Appointment;
import java.util.ArrayList;

public class PatientAppointmentListEvent extends AppointmentListEvent{

  public enum Status{
    ACCEPTED,
    REJECT,
    SHOW_APPOINTMENT_DATA
  }

  public Status status;

  public PatientAppointmentListEvent(ArrayList<Appointment> appointments, Object sender) {
    super(appointments, sender);
  }
}
