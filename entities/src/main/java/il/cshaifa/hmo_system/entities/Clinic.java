package il.cshaifa.hmo_system.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clinics")
public class Clinic implements Serializable {
  public static final long serialVersionUID = 3L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String name;
  private String address;
  private String sun_hours;
  private String mon_hours;
  private String tue_hours;
  private String wed_hours;
  private String thu_hours;
  private String fri_hours;
  private String sat_hours;

  public Clinic() {}

  public Clinic(
      String name,
      String address,
      String sun_hours,
      String mon_hours,
      String tue_hours,
      String wed_hours,
      String thu_hours,
      String fri_hours,
      String sat_hours) {
    this.name = name;
    this.address = address;
    this.sun_hours = sun_hours;
    this.mon_hours = mon_hours;
    this.tue_hours = tue_hours;
    this.wed_hours = wed_hours;
    this.thu_hours = thu_hours;
    this.fri_hours = fri_hours;
    this.sat_hours = sat_hours;
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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getSun_hours() {
    return sun_hours;
  }

  public void setSun_hours(String sun_hours) {
    this.sun_hours = sun_hours;
  }

  public String getMon_hours() {
    return mon_hours;
  }

  public void setMon_hours(String mon_hours) {
    this.mon_hours = mon_hours;
  }

  public String getTue_hours() {
    return tue_hours;
  }

  public void setTue_hours(String tue_hours) {
    this.tue_hours = tue_hours;
  }

  public String getWed_hours() {
    return wed_hours;
  }

  public void setWed_hours(String wed_hours) {
    this.wed_hours = wed_hours;
  }

  public String getThu_hours() {
    return thu_hours;
  }

  public void setThu_hours(String thu_hours) {
    this.thu_hours = thu_hours;
  }

  public String getFri_hours() {
    return fri_hours;
  }

  public void setFri_hours(String fri_hours) {
    this.fri_hours = fri_hours;
  }

  public String getSat_hours() {
    return sat_hours;
  }

  public void setSat_hours(String sat_hours) {
    this.sat_hours = sat_hours;
  }

  public void setClinicWorkHours(int day, String workHours) {
    switch (day) {
      case (1):
        setSun_hours(workHours);
        return;
      case (2):
        setMon_hours(workHours);
        return;
      case (3):
        setTue_hours(workHours);
        return;
      case (4):
        setWed_hours(workHours);
        return;
      case (5):
        setThu_hours(workHours);
        return;
      case (6):
        setFri_hours(workHours);
        return;
      case (7):
        setSat_hours(workHours);
        return;
      default:
    }
  }
}
