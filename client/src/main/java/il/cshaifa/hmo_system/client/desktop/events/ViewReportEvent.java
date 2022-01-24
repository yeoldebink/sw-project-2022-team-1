package il.cshaifa.hmo_system.client.desktop.events;

import il.cshaifa.hmo_system.client.events.Event;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import il.cshaifa.hmo_system.reports.DailyReport;
import java.util.List;

public class ViewReportEvent extends Event {
  public List<DailyReport> reports;
  public ReportType reportType;

  public ViewReportEvent(Object sender, List<DailyReport> reports, ReportType reportType) {
    super(sender);
    this.reports = reports;
    this.reportType = reportType;
  }
}
