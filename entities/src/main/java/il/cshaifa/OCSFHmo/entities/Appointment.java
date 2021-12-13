package il.ac.haifa.client_server.entities.src.main.java.il.cshaifa.OCSFHmo.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int patient_id;
  private int type_id;
  private int specialist_role_id;
  private int staff_member_id;
  private int clinic_id;
  private LocalDateTime date;
  private boolean taken;
  private String comments;
  private LocalDateTime lock_time;

  public Appointment(
      int patient_id,
      int type_id,
      int specialist_role_id,
      int staff_member_id,
      int clinic_id,
      LocalDateTime date) {
    this.patient_id = patient_id;
    this.type_id = type_id;
    this.specialist_role_id = specialist_role_id;
    this.staff_member_id = staff_member_id;
    this.clinic_id = clinic_id;
    this.date = date;
    this.taken = false;
    this.comments = null;
    this.lock_time = null;
  }

  public int getId() {
    return id;
  }

  public int getPatient_id() {
    return patient_id;
  }

  public void setPatient_id(int patient_id) {
    this.patient_id = patient_id;
  }

  public int getType_id() {
    return type_id;
  }

  public void setType_id(int type_id) {
    this.type_id = type_id;
  }

  public int getSpecialist_role_id() {
    return specialist_role_id;
  }

  public void setSpecialist_role_id(int specialist_role_id) {
    this.specialist_role_id = specialist_role_id;
  }

  public int getStaff_member_id() {
    return staff_member_id;
  }

  public void setStaff_member_id(int staff_member_id) {
    this.staff_member_id = staff_member_id;
  }

  public int getClinic_id() {
    return clinic_id;
  }

  public void setClinic_id(int clinic_id) {
    this.clinic_id = clinic_id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
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
