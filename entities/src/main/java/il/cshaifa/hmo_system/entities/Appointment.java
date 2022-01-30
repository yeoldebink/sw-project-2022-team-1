package il.cshaifa.hmo_system.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne private Patient patient;

  @ManyToOne private AppointmentType type;

  @ManyToOne private Role specialist_role;

  @ManyToOne private User staff_member;

  @ManyToOne private Clinic clinic;

  private LocalDateTime appt_date;
  private boolean taken;
  private String comments;
  private LocalDateTime lock_time;
  private LocalDateTime called_time;
  private boolean arrived;

  public Appointment() {}

  public Appointment(
      Patient patient_id,
      AppointmentType type_id,
      Role specialist_role_id,
      User staff_member_id,
      Clinic clinic_id,
      LocalDateTime appt_date,
      LocalDateTime called_time,
      LocalDateTime lock_time,
      boolean taken,
      boolean arrived) {
    this.patient = patient_id;
    this.type = type_id;
    this.specialist_role = specialist_role_id;
    this.staff_member = staff_member_id;
    this.clinic = clinic_id;
    this.appt_date = appt_date;
    this.called_time = called_time;
    this.lock_time = lock_time;
    this.taken = taken;
    this.comments = null;
    this.lock_time = null;
    this.arrived = arrived;
  }

  public int getId() {
    return id;
  }

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  public AppointmentType getType() {
    return type;
  }

  public void setType(AppointmentType type) {
    this.type = type;
  }

  public Role getSpecialist_role() {
    return specialist_role;
  }

  public void setSpecialist_role(Role specialist_role) {
    this.specialist_role = specialist_role;
  }

  public User getStaff_member() {
    return staff_member;
  }

  public void setStaff_member(User staff_member) {
    this.staff_member = staff_member;
  }

  public Clinic getClinic() {
    return clinic;
  }

  public void setClinic(Clinic clinic) {
    this.clinic = clinic;
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

  public LocalDateTime getCalled_time() {
    return called_time;
  }

  public void setCalled_time(LocalDateTime called_time) {
    this.called_time = called_time;
  }

  public boolean hasArrived() {
    return arrived;
  }

  public void setArrived(boolean arrived) {
    this.arrived = arrived;
  }
}
