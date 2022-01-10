package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.events.ReportEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import org.greenrobot.eventbus.Subscribe;

public class ReportListController extends Controller {
  public ReportListController(ReportListViewController view_controller) {
    super(view_controller, null);
  }

  /**
   * Event the handle user request to see reports
   * @param event GUI data on which reports the user requested
   */
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

  /**
   * Event that handle the client respond with reports and populate the reports view
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
   * @param clinics List of clinics that returned from the server
   */
  public void updateClinics(ArrayList<Clinic> clinics) {
    Platform.runLater(
        () -> ((ReportListViewController) this.view_controller).populateClinicList(clinics));
  }
}
