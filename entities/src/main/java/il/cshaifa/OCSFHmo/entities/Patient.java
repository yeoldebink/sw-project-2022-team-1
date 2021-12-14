package il.cshaifa.OCSFHmo.entities;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int user_id;
  private int home_clinic_id;
  private LocalDateTime birthday;

  public Patient(int user_id, int home_clinic_id, LocalDateTime birthday) {
    this.user_id = user_id;
    this.home_clinic_id = home_clinic_id;
    this.birthday = birthday;
  }

  public int getUser_id() {
    return user_id;
  }

  public void setUser_id(int user_id) {
    this.user_id = user_id;
  }

  public int getHome_clinic_id() {
    return home_clinic_id;
  }

  public void setHome_clinic_id(int home_clinic_id) {
    this.home_clinic_id = home_clinic_id;
  }

  public LocalDateTime getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDateTime birthday) {
    this.birthday = birthday;
  }
}
