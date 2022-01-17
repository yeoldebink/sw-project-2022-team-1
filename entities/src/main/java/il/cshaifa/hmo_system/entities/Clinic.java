package il.cshaifa.hmo_system.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "clinics")
public class Clinic implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToOne private User manager_user;

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
      User manager_user,
      String name,
      String address,
      String sun_hours,
      String mon_hours,
      String tue_hours,
      String wed_hours,
      String thu_hours,
      String fri_hours,
      String sat_hours) {
    this.manager_user = manager_user;
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

  public User getManager_user() {
    return manager_user;
  }

  public void setManager_user(User manager_user) {
    this.manager_user = manager_user;
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

  public List<LocalTime> timeStringToLocalTimeList(int day_num){
    String day_hours;
    // 1 is monday
    switch (day_num){
      case 1: day_hours = mon_hours;
        break;
      case 2: day_hours = tue_hours;
        break;
      case 3: day_hours = wed_hours;
        break;
      case 4: day_hours = thu_hours;
        break;
      case 5: day_hours = fri_hours;
        break;
      case 6: day_hours = sat_hours;
        break;
      case 7: day_hours = sun_hours;
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + day_num);
    }

    List<LocalTime> result = new ArrayList<>();
    String[] hours = day_hours.strip().split(", ");
    for (String time_window : hours){
      String open, close;
      String[] open_close = time_window.split("-");
      open = open_close[0];
      close = open_close[1];
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m");
      result.add(LocalTime.parse(open, formatter));
      result.add(LocalTime.parse(close, formatter));
    }
    return result;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
