package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.events.ReportEvent;
import il.cshaifa.hmo_system.client.events.ViewReportEvent;
import il.cshaifa.hmo_system.client.utils.Utils;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import il.cshaifa.hmo_system.reports.DailyAppointmentTypesReport;
import il.cshaifa.hmo_system.reports.DailyAverageWaitTimeReport;
import il.cshaifa.hmo_system.reports.DailyReport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import org.greenrobot.eventbus.Subscribe;

public class ReportListController extends Controller {
  public ReportListController(ReportListViewController view_controller) {
    super(view_controller, null);
  }

  @Subscribe
  public void onReportsRequest(ReportEvent event) {
    if (!event.getSender().equals(this.view_controller) || event.clinics.size() == 0) return;
    try {
      HMOClient.getClient()
          .requestReports(event.clinics, event.start_date, event.end_date, event.type);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onReportRespond(ReportEvent event) {
    if (!event.getSender().equals(HMOClient.getClient()) || event.reports.size() == 0) return;

    Platform.runLater(
        () ->
            ((ReportListViewController) this.view_controller).populateReportsTable(event.reports));
  }

  public void updateClinics(ArrayList<Clinic> clinics) {
    Platform.runLater(
        () -> ((ReportListViewController) this.view_controller).populateClinicList(clinics));
  }

  @Subscribe
  public void onReportSelection(ViewReportEvent event) {
    // generate an aggregate report based on type
    DailyReport aggregateReport;

    if (event.reportType == ReportType.AVERAGE_WAIT_TIMES) {
      aggregateReport = aggregateDailyAverageWaitTimeReport(event.reports);
    } else {
      aggregateReport = aggregateDailyAppointmentTypesReport(event.reports);
    }
  }

  private DailyAppointmentTypesReport aggregateDailyAppointmentTypesReport(List<DailyReport> reports) {

  }

  private DailyAverageWaitTimeReport aggregateDailyAverageWaitTimeReport(List<DailyReport> reports) {

  }
}
