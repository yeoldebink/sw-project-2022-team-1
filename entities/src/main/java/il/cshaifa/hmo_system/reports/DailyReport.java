package il.cshaifa.hmo_system.reports;

import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class DailyReport implements Serializable {
  public LocalDateTime date;
  public Clinic clinic;

  public DailyReport(LocalDateTime date, Clinic clinic) {
    this.date = date;
    this.clinic = clinic;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getClinicName() {
    return clinic.getName();
  }
}
