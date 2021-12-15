package il.cshaifa.hmo_system.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

// TODO: Composite primary key?

@Entity
@Table(name = "clinic_staff")
public class ClinicStaff {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne private Clinic clinic;
  @ManyToOne private User user;

  public ClinicStaff() {}

  public ClinicStaff(int id, Clinic clinic_id, User user_id) {
    this.id = id;
    this.clinic = clinic_id;
    this.user = user_id;
  }

  public int getId() {
    return id;
  }

  public Clinic getClinic() {
    return clinic;
  }

  public void setClinic(Clinic clinic) {
    this.clinic = clinic;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
