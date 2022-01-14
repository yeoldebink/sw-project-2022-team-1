package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.reports.DailyReport;
import java.time.LocalDateTime;
import java.util.List;

public class ReportMessage extends Message {
  public enum ReportType {
    APPOINTMENT_ATTENDANCE,
    AVERAGE_WAIT_TIMES,
    MISSED_APPOINTMENTS
  }

  public List<DailyReport> reports;
  public List<Clinic> clinics;
  public ReportType report_type;
  public LocalDateTime start_date, end_date;

  public ReportMessage(
      List<Clinic> clinics,
      ReportType report_type,
      LocalDateTime start_date,
      LocalDateTime end_date) {
    super(MessageType.REQUEST);
    this.clinics = clinics;
    this.report_type = report_type;
    this.start_date = start_date;
    this.end_date = end_date;
  }
}
