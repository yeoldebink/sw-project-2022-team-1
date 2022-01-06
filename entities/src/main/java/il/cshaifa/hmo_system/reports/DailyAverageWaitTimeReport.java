package il.cshaifa.hmo_system.reports;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.time.LocalDateTime;
import java.util.Map;

public class DailyAverageWaitTimeReport extends DailyReport {
  public Map<User, Integer> report_data;

  public DailyAverageWaitTimeReport(LocalDateTime date,
      Clinic clinic) {
    super(date, clinic);
  }
}
