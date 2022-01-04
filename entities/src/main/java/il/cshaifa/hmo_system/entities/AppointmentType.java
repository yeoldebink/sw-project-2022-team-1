package il.cshaifa.hmo_system.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "appointment_types")
public class AppointmentType implements Serializable {
  @Id private String name;

  public AppointmentType() {}

  public AppointmentType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
