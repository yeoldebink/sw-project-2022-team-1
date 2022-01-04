package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.reports.DailyReport;
import java.time.LocalDateTime;
import java.util.List;

public class ReportMessage extends Message {
  public enum ReportType {
    MISSED_APPOINTMENTS,
    AVERAGE_WAIT_TIMES,
    APPOINTMENT_ATTENDANCE
  }

  List<DailyReport> reports;
  List<Clinic> clinics;
  ReportType report_type;
  LocalDateTime start_date, end_date;

  public ReportMessage() { // TODO: args?
    super(MessageType.REQUEST);
  }
}
