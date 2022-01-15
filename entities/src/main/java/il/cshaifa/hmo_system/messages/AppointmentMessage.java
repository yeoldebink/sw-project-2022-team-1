package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import java.util.ArrayList;
import java.util.List;

public class AppointmentMessage extends Message {
  public User user;
  public Patient patient;
  public Clinic clinic;
  public AppointmentType type;
  public AppointmentRequestType requestType;
  public List<Appointment> appointments;

  public enum AppointmentRequestType {
    CLINIC_APPOINTMENTS,
    STAFF_MEMBER_DAILY_APPOINTMENTS,
    STAFF_FUTURE_APPOINTMENTS,
    PATIENT_HISTORY,
    CREATE_APPOINTMENTS,
    DELETE_APPOINTMENTS
  }

  /** constructor for use of staff member/manager */
  public AppointmentMessage(User user, AppointmentRequestType request) {
    super(MessageType.REQUEST);
    this.user = user;
    this.requestType = request;
  }

  /** constructor for use of patient to request free appointments/appointment history */
  public AppointmentMessage(Patient patient, AppointmentRequestType request) {
    super(MessageType.REQUEST);
    this.patient = patient;
    this.requestType = request;
  }

}
