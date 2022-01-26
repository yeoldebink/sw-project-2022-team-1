package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import java.time.LocalDateTime;

public class AppointmentForAdminTableView {

  Integer id;
  String type_name;
  LocalDateTime appt_date;
  LocalDateTime called_time;
  String comments;
  Boolean taken;
  String patient_name;

  public AppointmentForAdminTableView(
      Integer id,
      AppointmentType type,
      LocalDateTime appt_date,
      LocalDateTime called_time,
      String comments,
      Boolean taken,
      Patient patient) {
    this.id = id;
    this.type_name = type.getName();
    this.appt_date = appt_date;
    this.called_time = called_time;
    this.comments = comments;
    this.taken = taken;
    if (patient != null && taken) { // only display patient name for taken appointments
      this.patient_name = patient.getUser().getFirstName() + " " + patient.getUser().getLastName();
    } else {
      this.patient_name = "";
    }
  }

  public Integer getId() {
    return id;
  }

  public LocalDateTime getAppt_date() {
    return appt_date;
  }

  public LocalDateTime getCalled_time() {
    return called_time;
  }

  public String getComments() {
    return comments;
  }

  public Boolean getTaken() {
    return taken;
  }

  public String getPatient_name() {
    return patient_name;
  }

  public String getAppt_type() {
    return type_name;
  }
}
