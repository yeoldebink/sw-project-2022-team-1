package il.cshaifa.hmo_system.reports;

import il.cshaifa.hmo_system.entities.AppointmentType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class DailyReport implements Serializable {
  LocalDateTime date;
  Map<AppointmentType, Integer> report_data;

  // TODO: constructor
}
