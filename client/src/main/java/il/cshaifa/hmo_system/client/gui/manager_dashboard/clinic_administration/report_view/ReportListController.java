package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.events.CloseWindowEvent;

public class ReportListController extends Controller {
  public ReportListController(ReportListViewController view_controller) {
    super(view_controller, null);
  }

  @Override
  public void onWindowCloseEvent(CloseWindowEvent event) {}
}
