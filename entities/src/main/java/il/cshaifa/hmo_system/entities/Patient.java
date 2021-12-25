package il.cshaifa.hmo_system.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient implements Serializable {

  @Id @OneToOne private User user;

  @ManyToOne private Clinic home_clinic;

  private LocalDateTime birthday;

  public Patient() {}

  public Patient(User user_id, Clinic home_clinic_id, LocalDateTime birthday) {
    this.user = user_id;
    this.home_clinic = home_clinic_id;
    this.birthday = birthday;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Clinic getHome_clinic() {
    return home_clinic;
  }

  public void setHome_clinic(Clinic home_clinic) {
    this.home_clinic = home_clinic;
  }

  public LocalDateTime getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDateTime birthday) {
    this.birthday = birthday;
  }
}
