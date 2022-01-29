package il.cshaifa.hmo_system.entities;

import il.cshaifa.hmo_system.Utils;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User implements Serializable {
  @Id private int id;
  private String password;
  private String salt;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  @ManyToOne private Role role;

  /* In case of getting a User from DB */
  public User() {}

  /* In case of create a new User for DB */
  public User(
      int id,
      String password,
      String firstName,
      String lastName,
      String email,
      String phone,
      Role role_id)
      throws NoSuchAlgorithmException {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.role = role_id;
    this.salt = Utils.generateSalt();
    this.password = Utils.encodePassword(password, this.salt);
  }

  public User(User user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.email = user.getEmail();
    this.phone = user.getPhone();
    this.role = user.getRole();
    this.salt = user.getSalt();
    this.password = user.getPassword();
  }

  public int getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) throws NoSuchAlgorithmException {
    this.salt = Utils.generateSalt();
    this.password = Utils.encodePassword(password, this.salt);
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getSalt() {
    return salt;
  }

  @Override
  public String toString() {
    return firstName + " " + lastName;
  }

  @Override
  public boolean equals(Object user) {
    return this.id == ((User) user).getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
