package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.CommonEnums.AddAppointmentRejectionReason;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.time.LocalDateTime;
import java.util.List;

public class AdminAppointmentMessage extends Message {
  public enum RequestType {
    CREATE,
    DELETE
  }

  public User staff_member;
  public Clinic clinic;
  public LocalDateTime start_datetime;
  public int count;
  public AppointmentType appt_type;
  public List<Appointment> appointments;
  public RequestType request;
  public boolean success;
  public AddAppointmentRejectionReason reject;

  public AdminAppointmentMessage(
      RequestType request,
      User staff_member,
      Clinic clinic,
      LocalDateTime start_datetime,
      int count,
      List<Appointment> appointments,
      AppointmentType appt_type) {
    super(MessageType.REQUEST);
    this.request = request;
    this.staff_member = staff_member;
    this.clinic = clinic;
    this.start_datetime = start_datetime;
    this.count = count;
    this.appointments = appointments;
    this.appt_type = appt_type;
  }
}
