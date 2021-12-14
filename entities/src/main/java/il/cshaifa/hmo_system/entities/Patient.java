package il.cshaifa.hmo_system.entities;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {
  @Id @OneToOne private User user_id;

  @ManyToOne private Clinic home_clinic_id;

  private LocalDateTime birthday;

  public Patient(User user_id, Clinic home_clinic_id, LocalDateTime birthday) {
    this.user_id = user_id;
    this.home_clinic_id = home_clinic_id;
    this.birthday = birthday;
  }

  public User getUser_id() {
    return user_id;
  }

  public void setUser_id(User user_id) {
    this.user_id = user_id;
  }

  public Clinic getHome_clinic_id() {
    return home_clinic_id;
  }

  public void setHome_clinic_id(Clinic home_clinic_id) {
    this.home_clinic_id = home_clinic_id;
  }

  public LocalDateTime getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDateTime birthday) {
    this.birthday = birthday;
  }
}
