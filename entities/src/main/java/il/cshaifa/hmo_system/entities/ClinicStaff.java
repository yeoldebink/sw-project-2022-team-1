package il.cshaifa.hmo_system.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: Composite primary key?

@Entity
@Table(name = "clinic_staff")
public class ClinicStaff {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int clinic_id;
  private int user_id;

  public ClinicStaff(int clinic_id, int user_id) {
    this.clinic_id = clinic_id;
    this.user_id = user_id;
  }

  public int getClinic_id() {
    return clinic_id;
  }

  public void setClinic_id(int clinic_id) {
    this.clinic_id = clinic_id;
  }

  public int getUser_id() {
    return user_id;
  }

  public void setUser_id(int user_id) {
    this.user_id = user_id;
  }
}
