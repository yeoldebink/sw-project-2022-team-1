package il.ac.haifa.client_server.entities.src.main.java.il.cshaifa.OCSFHmo.entities;

import javax.persistence.*;

@Entity
@Table(name = "appointment_types")
public class AppointmentType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String name;

  public AppointmentType(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
