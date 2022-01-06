package il.cshaifa.hmo_system.reports;

import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import java.time.LocalDateTime;
import java.util.Map;

public class DailyAppointmentTypesReport extends DailyReport {
  Map<AppointmentType, Integer> report_data;

  public DailyAppointmentTypesReport(LocalDateTime date,
      Clinic clinic) {
    super(date, clinic);
  }
}
