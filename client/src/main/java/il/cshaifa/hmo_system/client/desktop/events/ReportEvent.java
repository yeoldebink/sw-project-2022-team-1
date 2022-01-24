package il.cshaifa.hmo_system.client.desktop.events;

import il.cshaifa.hmo_system.client.events.Event;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import il.cshaifa.hmo_system.reports.DailyReport;
import java.time.LocalDateTime;
import java.util.List;

public class ReportEvent extends Event {

  public List<Clinic> clinics;
  public ReportType type;
  public LocalDateTime start_date, end_date;
  public List<DailyReport> reports;
  public ClinicStaff staff_member;

  public ReportEvent(
      List<Clinic> clinics,
      ClinicStaff staff_member,
      ReportType type,
      LocalDateTime start_date,
      LocalDateTime end_date,
      List<DailyReport> reports,
      Object sender) {
    super(sender);
    this.clinics = clinics;
    this.staff_member = staff_member;
    this.type = type;
    this.start_date = start_date;
    this.end_date = end_date;
    this.reports = reports;
  }
}
