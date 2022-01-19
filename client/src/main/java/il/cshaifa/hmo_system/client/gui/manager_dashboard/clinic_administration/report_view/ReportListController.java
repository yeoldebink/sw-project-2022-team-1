package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.events.ReportEvent;
import il.cshaifa.hmo_system.client.events.ViewReportEvent;
import il.cshaifa.hmo_system.client.utils.Utils;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import il.cshaifa.hmo_system.reports.DailyAppointmentTypesReport;
import il.cshaifa.hmo_system.reports.DailyAverageWaitTimeReport;
import il.cshaifa.hmo_system.reports.DailyReport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import org.greenrobot.eventbus.Subscribe;

public class ReportListController extends Controller {
  public ReportListController(ReportListViewController view_controller) {
    super(view_controller, null);
  }

  /**
   * Event the handle user request to see reports
   *
   * @param event GUI data on which reports the user requested
   */
  @Subscribe
  public void onReportsRequest(ReportEvent event) {
    if (!event.getSender().equals(this.view_controller) || event.clinics.size() == 0) return;
    try {
      HMOClient.getClient()
          .requestReports(event.clinics, event.staff_member ,event.start_date, event.end_date, event.type);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Event that handle the client respond with reports and populate the reports view
   *
   * @param event Hold the reports that the user requested
   */
  @Subscribe
  public void onReportRespond(ReportEvent event) {
    if (!event.getSender().equals(HMOClient.getClient()) || event.reports.size() == 0) return;

    Platform.runLater(
        () ->
            ((ReportListViewController) this.view_controller).populateReportsTable(event.reports));
  }

  /**
   * Update the clinic list in the view
   *
   * @param clinics List of clinics that returned from the server
   */
  public void updateClinics(ArrayList<Clinic> clinics) {
    Platform.runLater(
        () -> ((ReportListViewController) this.view_controller).populateClinicList(clinics));
  }

  public void updateStaffMembers(ArrayList<ClinicStaff> staff_members){
    Platform.runLater(
        () -> ((ReportListViewController) this.view_controller).populateStaffList(staff_members));
  }
  @Subscribe
  public void onReportSelection(ViewReportEvent event) throws IOException {

    // load the view controller and its pane
    var loadedFXML =
        Utils.loadFXML(
            this.getClass(),
            ReportViewController.class,
            c -> new ReportViewController(event.reportType));

    if (event.reportType == ReportType.AVERAGE_WAIT_TIMES) {
      var aggregateReport = aggregateDailyAverageWaitTimeReport(event.reports);
      ReportController c = new ReportController(loadedFXML.view_controller, aggregateReport);
    } else {
      var aggregateReport = aggregateDailyAppointmentTypesReport(event.reports);
      ReportController c = new ReportController(loadedFXML.view_controller, aggregateReport);
    }

    ((ReportListViewController) view_controller).setViewedReport(loadedFXML.pane);
  }

  private DailyAppointmentTypesReport aggregateDailyAppointmentTypesReport(
      List<DailyReport> reports) {
    DailyAppointmentTypesReport aggregateReport = new DailyAppointmentTypesReport(null, null);

    // sum the reports into a single one
    for (var report : reports) {
      for (var entry : ((DailyAppointmentTypesReport) report).report_data.entrySet()) {
        if (!aggregateReport.report_data.containsKey(entry.getKey())) {
          aggregateReport.report_data.put(entry.getKey(), entry.getValue());
        } else {
          aggregateReport.report_data.put(
              entry.getKey(), aggregateReport.report_data.get(entry.getKey()) + entry.getValue());
        }
      }
    }

    return aggregateReport;
  }

  private DailyAverageWaitTimeReport aggregateDailyAverageWaitTimeReport(
      List<DailyReport> reports) {
    DailyAverageWaitTimeReport aggregateReport = new DailyAverageWaitTimeReport(null, null);

    // we need to keep track of how many days each staff member worked
    HashMap<User, Integer> daysWorked = new HashMap<>();

    var agReportData = aggregateReport.report_data;

    for (var report : reports) {
      for (var entry : ((DailyAverageWaitTimeReport) report).report_data.entrySet()) {
        var staffMember = entry.getKey();
        var waitTime = entry.getValue();

        // first encounter of staff member
        if (!daysWorked.containsKey(staffMember)) {
          daysWorked.put(staffMember, 1);
          agReportData.put(staffMember, entry.getValue());

        } else {
          var staffMemberDaysWorked = daysWorked.get(staffMember);

          // set new weighted average
          agReportData.put(
              staffMember,
              ((agReportData.get(staffMember) * staffMemberDaysWorked) + entry.getValue())
                  / (staffMemberDaysWorked + 1));

          daysWorked.put(staffMember, staffMemberDaysWorked + 1);
        }
      }
    }

    return aggregateReport;
  }
}
