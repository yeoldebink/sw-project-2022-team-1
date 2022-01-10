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
}
