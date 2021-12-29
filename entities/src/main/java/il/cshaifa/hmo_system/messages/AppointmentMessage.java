package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AppointmentMessage extends Message {
  public User user;
  public Clinic clinic;
  public AppointmentType type;
  public appointmentRequest requestType;
  public List<Appointment> appointments;

  public enum appointmentRequest {
    GET_CLINIC_APPOINTMENTS,
    SHOW_STAFF_APPOINTMENTS,
    SHOW_PATIENT_HISTORY,
    GENERATE_APPOINTMENTS
  }

  public AppointmentMessage(ArrayList<Appointment> appointments){
    super(messageType.REQUEST);
    this.appointments = appointments;
    this.requestType = appointmentRequest.GENERATE_APPOINTMENTS;
  }

  public AppointmentMessage(User user, appointmentRequest request) {
    super(messageType.REQUEST);
    this.user = user;
    this.requestType = request;
  }
}
