package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.util.List;

public class AppointmentMessage extends Message {
  public User user;
  public Clinic clinic;
  public AppointmentType type;
  public appointmentRequest requestType;
  public List<Appointment> appointments;

  public enum appointmentRequest {
    SCHEDULE_APPOINTMENT,
    SHOW_STAFF_APPOINTMENTS,
    SHOW_PATIENT_HISTORY
  }

  public AppointmentMessage(User user, appointmentRequest request) {
    super(messageType.REQUEST);
    this.user = user;
    this.requestType = request;
  }
}
