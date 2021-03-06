package il.cshaifa.hmo_system.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private boolean is_specialist;

  public Role() {}

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

  public boolean isSpecialist() {
    return is_specialist;
  }

  public void setIs_specialist(boolean is_specialist) {
    this.is_specialist = is_specialist;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Role && ((Role) o).name.equals(name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
