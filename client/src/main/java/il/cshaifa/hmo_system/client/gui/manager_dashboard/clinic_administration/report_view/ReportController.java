package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.Event;
import il.cshaifa.hmo_system.reports.DailyAppointmentTypesReport;
import il.cshaifa.hmo_system.reports.DailyAverageWaitTimeReport;
import org.greenrobot.eventbus.Subscribe;

public class ReportController extends Controller {

  private ReportController(ViewController view_controller) {
    super(view_controller, null);
  }

  public ReportController(ViewController view_controller, DailyAppointmentTypesReport report) {
    super(view_controller, null);
    ((ReportViewController) view_controller).populateReportTable(report.report_data);
  }

  public ReportController(ViewController view_controller, DailyAverageWaitTimeReport report) {
    super(view_controller, null);
    ((ReportViewController) view_controller).populateReportTable(report.report_data);
  }

  @Subscribe
  public void dummy(Event event) {}

  @Override
  public void onWindowClose() {}
}
