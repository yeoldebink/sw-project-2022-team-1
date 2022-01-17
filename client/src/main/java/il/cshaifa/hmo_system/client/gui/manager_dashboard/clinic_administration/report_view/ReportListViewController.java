package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.client.events.ReportEvent;
import il.cshaifa.hmo_system.client.events.ViewReportEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import il.cshaifa.hmo_system.reports.DailyReport;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;

public class ReportListViewController extends RoleDefinedViewController {

  @FXML private SplitPane splitPane;
  @FXML private TableView<DailyReport> reportsTable;

  @FXML private TableColumn<DailyReport, String> clinicNameTableColumn;
  @FXML private TableColumn<DailyReport, LocalDateTime> reportDateTableColumn;

  @FXML private ListView<Clinic> clinicList;

  @FXML private DatePicker startDatePicker, endDatePicker;

  @FXML private ComboBox<ReportTypeComboBoxItem> reportTypeComboBox;

  public ReportListViewController(Role role) {
    super(role);
  }

  @Override
  protected void applyRoleBehavior() {
    if (role.getName().equals("Clinic Manager")) {
      clinicList.getItems().setAll(HMOClient.getClient().getConnected_employee_clinics());
      clinicList.getSelectionModel().select(0);
      clinicList.setDisable(true);
    }
  }

  @FXML
  public void initialize() {
    clinicList.setCellFactory(
        clinic ->
            new ListCell<>() {
              @Override
              protected void updateItem(Clinic clinic, boolean b) {
                super.updateItem(clinic, b);
                setText(clinic == null ? null : clinic.getName());
              }
            });

    clinicList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    reportsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    clinicNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("ClinicName"));
    reportDateTableColumn.setCellValueFactory(new PropertyValueFactory<>("Date"));
    reportsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    reportTypeComboBox
        .getItems()
        .add(
            new ReportTypeComboBoxItem(
                ReportType.APPOINTMENT_ATTENDANCE, "Appointment attendance"));

    reportTypeComboBox
        .getItems()
        .add(new ReportTypeComboBoxItem(ReportType.MISSED_APPOINTMENTS, "Missed appointments"));

    reportTypeComboBox
        .getItems()
        .add(new ReportTypeComboBoxItem(ReportType.AVERAGE_WAIT_TIMES, "Average wait times"));

    reportTypeComboBox.getSelectionModel().select(0);
  }

  public void populateReportsTable(List<DailyReport> reports) {

    reports.sort(Comparator.comparing(DailyReport::getDate));

    reportsTable.getItems().setAll(reports);
  }

  public void populateClinicList(List<Clinic> clinics) {
    if (!clinicList.isDisabled()) {
      clinics.sort(Comparator.comparing(Clinic::toString));
      clinicList.getItems().setAll(clinics);

      applyRoleBehavior();
    }
  }

  @FXML
  public void requestReports(ActionEvent event) {
    // clear the pane and the list
    splitPane.getItems().set(2, new Pane());
    reportsTable.getItems().clear();

    var selected_clinics = new ArrayList<>(clinicList.getSelectionModel().getSelectedItems());

    var report_type = reportTypeComboBox.getValue().reportType;
    var startDate = startDatePicker.getValue().atStartOfDay();

    // we want to get reports INCLUDING the last day
    var endDate = endDatePicker.getValue().atTime(23, 59, 59);

    var report_event =
        new ReportEvent(selected_clinics, report_type, startDate, endDate, null, this);

    EventBus.getDefault().post(report_event);
  }

  @FXML
  public void onMouseClickedInReportsTable(MouseEvent event) {
    // clear the pane
    splitPane.getItems().set(2, new Pane());

    var selected_reports = reportsTable.getSelectionModel().getSelectedItems();
    if (selected_reports.size() > 0) {
      EventBus.getDefault()
          .post(
              new ViewReportEvent(
                  this,
                  reportsTable.getSelectionModel().getSelectedItems(),
                  reportTypeComboBox.getValue().reportType));
    }
  }

  public void setViewedReport(Pane reportPane) {
    this.splitPane.getItems().set(2, reportPane);
  }

  static class ReportTypeComboBoxItem {
    public ReportType reportType;
    public String reportTypeName;

    public ReportTypeComboBoxItem(ReportType reportType, String reportTypeName) {
      this.reportType = reportType;
      this.reportTypeName = reportTypeName;
    }

    @Override
    public String toString() {
      return this.reportTypeName;
    }
  }
}
