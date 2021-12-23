package il.cshaifa.hmo_system.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

  public User() {}

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

    this.password = encodePassword(password);
  }

  public int getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) throws NoSuchAlgorithmException {
    this.password = encodePassword(password);
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

  public void setSalt(byte[] salt) {
    this.salt = new String(salt);
  }

  /** Before storing password in DB, we encoded it */
  private String encodePassword(String password) throws NoSuchAlgorithmException {
    /* Generate random salt */
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    setSalt(salt);

    /* Encode password+salt and return */
    MessageDigest md = MessageDigest.getInstance("SHA-512");
    byte[] messageDigest = md.digest((password + this.salt).getBytes());
    BigInteger no = new BigInteger(1, messageDigest);
    StringBuilder hashtext = new StringBuilder(no.toString(16));
    while (hashtext.length() < 32) hashtext.insert(0, "0");
    return hashtext.toString();
  }
}
