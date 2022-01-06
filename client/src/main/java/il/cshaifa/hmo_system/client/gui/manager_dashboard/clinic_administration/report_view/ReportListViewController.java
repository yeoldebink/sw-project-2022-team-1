package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.reports.DailyReport;
import java.time.LocalDateTime;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReportListViewController extends RoleDefinedViewController {

  @FXML private TableView<DailyReport> reportsTable;

  @FXML private TableColumn<DailyReport, String> clinicNameTableColumn;
  @FXML private TableColumn<DailyReport, LocalDateTime> reportDateTableColumn;

  @FXML private ListView<CheckBox> clinicList;

  public ReportListViewController(Role role) {
    super(role);
  }

  @Override
  protected void applyRoleBehavior() {}

  @FXML
  public void initialize() {
    clinicNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("ClinicName"));
    reportDateTableColumn.setCellValueFactory(new PropertyValueFactory<>("Date"));

    clinicList.getItems().add(new CheckBox("hello!"));
  }

  public void populateReportsTable(List<DailyReport> reports) {
    reportsTable.getItems().setAll(reports);
  }

  @FXML
  public void requestReports(ActionEvent event) {}
}
