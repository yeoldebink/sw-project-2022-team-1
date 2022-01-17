package il.cshaifa.hmo_system.reports;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Role;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DailyAppointmentTypesReport extends DailyReport {
  public Map<String, Integer> report_data;

  public DailyAppointmentTypesReport(LocalDateTime date, Clinic clinic) {
    super(date, clinic);
    report_data = new HashMap<>();
  }
}
