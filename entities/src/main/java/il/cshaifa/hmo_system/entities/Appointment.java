package il.cshaifa.hmo_system.entities;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne private Patient patient_id;

  @ManyToOne private AppointmentType type_id;

  @ManyToOne private Role specialist_role_id;

  @ManyToOne private User staff_member_id;

  @ManyToOne private Clinic clinic_id;

  private LocalDateTime appt_date;
  private boolean taken;
  private String comments;
  private LocalDateTime lock_time;

  public Appointment(
      Patient patient_id,
      AppointmentType type_id,
      Role specialist_role_id,
      User staff_member_id,
      Clinic clinic_id,
      LocalDateTime appt_date) {
    this.patient_id = patient_id;
    this.type_id = type_id;
    this.specialist_role_id = specialist_role_id;
    this.staff_member_id = staff_member_id;
    this.clinic_id = clinic_id;
    this.appt_date = appt_date;
    this.taken = false;
    this.comments = null;
    this.lock_time = null;
  }

  public int getId() {
    return id;
  }

  public Patient getPatient_id() {
    return patient_id;
  }

  public void setPatient_id(Patient patient_id) {
    this.patient_id = patient_id;
  }

  public AppointmentType getType_id() {
    return type_id;
  }

  public void setType_id(AppointmentType type_id) {
    this.type_id = type_id;
  }

  public Role getSpecialist_role_id() {
    return specialist_role_id;
  }

  public void setSpecialist_role_id(Role specialist_role_id) {
    this.specialist_role_id = specialist_role_id;
  }

  public User getStaff_member_id() {
    return staff_member_id;
  }

  public void setStaff_member_id(User staff_member_id) {
    this.staff_member_id = staff_member_id;
  }

  public Clinic getClinic_id() {
    return clinic_id;
  }

  public void setClinic_id(Clinic clinic_id) {
    this.clinic_id = clinic_id;
  }

  public LocalDateTime getDate() {
    return appt_date;
  }

  public void setDate(LocalDateTime appt_date) {
    this.appt_date = appt_date;
  }

  public boolean isTaken() {
    return taken;
  }

  public void setTaken(boolean taken) {
    this.taken = taken;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public LocalDateTime getLock_time() {
    return lock_time;
  }

  public void setLock_time(LocalDateTime lock_time) {
    this.lock_time = lock_time;
  }
}
