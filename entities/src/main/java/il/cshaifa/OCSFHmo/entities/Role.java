package il.ac.haifa.client_server.entities.src.main.java.il.cshaifa.OCSFHmo.entities;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String name;
  private boolean is_specialist;

  public Role(String name, boolean is_specialist) {
    this.name = name;
    this.is_specialist = is_specialist;
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

  public boolean isIs_specialist() {
    return is_specialist;
  }

  public void setIs_specialist(boolean is_specialist) {
    this.is_specialist = is_specialist;
  }
}
