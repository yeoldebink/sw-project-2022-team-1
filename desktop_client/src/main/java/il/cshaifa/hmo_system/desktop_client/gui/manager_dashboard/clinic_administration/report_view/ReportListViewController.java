package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client_base.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.desktop_client.HMODesktopClient;
import il.cshaifa.hmo_system.desktop_client.events.ReportEvent;
import il.cshaifa.hmo_system.desktop_client.events.ViewReportEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifa.hmo_system.Constants.CLINIC_MANAGER;
import static il.cshaifa.hmo_system.Constants.HMO_MANAGER;
import static il.cshaifa.hmo_system.Constants.ROLE;

public class ReportListViewController extends RoleDefinedViewController {

  @FXML private AnchorPane clinicListPane;
  @FXML private AnchorPane staffListPane;
  @FXML private StackPane listStackPane;
  @FXML private ListView<ClinicStaff> staffList;
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
    if (role.equals(ROLE(CLINIC_MANAGER))) {
      clinicList.getItems().setAll(HMODesktopClient.getClient().getConnected_employee_clinics());
      clinicList.getSelectionModel().select(0);
      clinicList.setDisable(true);
    }
  }

  /**
   * Updates view by report type and User role
   */
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

    staffList.setCellFactory(
        staff ->
            new ListCell<>() {
              @Override
              protected void updateItem(ClinicStaff staff, boolean b) {
                super.updateItem(staff, b);
                setText(
                    staff == null
                        ? null
                        : staff.getUser().toString());
              }
            });

    staffList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

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

    reportTypeComboBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              if (newValue != null) {
                var isHMOManager =
                    HMODesktopClient.getClient()
                        .getConnected_user()
                        .getRole()
                        .equals(ROLE(HMO_MANAGER));
                if (newValue == reportTypeComboBox.getItems().get(2)) {
                  if (isHMOManager) {
                    clinicList.getSelectionModel().selectAll();
                    clinicList.setDisable(true);
                  }
                  switchToPane(staffListPane);
                } else {
                  if (isHMOManager) {
                    clinicList.getSelectionModel().clearSelection();
                    clinicList.setDisable(false);
                  }
                  switchToPane(clinicListPane);
                }
              }
            });

    reportTypeComboBox.getSelectionModel().select(0);

    reportsTable
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              splitPane.getItems().set(2, new Pane());
              if (newValue != null) {
                EventBus.getDefault()
                    .post(
                        new ViewReportEvent(
                            this,
                            reportsTable.getSelectionModel().getSelectedItems(),
                            reportTypeComboBox.getValue().reportType));
              }
            });

    switchToPane(clinicListPane);
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

  public void populateStaffList(List<ClinicStaff> staff_members) {
    staff_members.sort(Comparator.comparing(ClinicStaff::toString));
    staffList.getItems().setAll(staff_members);
  }

  public void switchToPane(Object pane) {
    for (var p : listStackPane.getChildren()) {
      p.setVisible(p.equals(pane));
    }
  }

  /**
   * Emits event requesting reports chosen through the view
   * @param event
   */
  @FXML
  public void requestReports(ActionEvent event) {
    // clear the pane and the list
    splitPane.getItems().set(2, new Pane());
    reportsTable.getItems().clear();
    Event report_event;

    var report_type = reportTypeComboBox.getValue().reportType;
    var startDate = startDatePicker.getValue().atStartOfDay();

    // we want to get reports INCLUDING the last day
    var endDate = endDatePicker.getValue().atTime(23, 59, 59);
    var selected_clinics = new ArrayList<>(clinicList.getSelectionModel().getSelectedItems());

    if (report_type != ReportType.AVERAGE_WAIT_TIMES) {
      report_event =
          new ReportEvent(selected_clinics, null, report_type, startDate, endDate, null, this);
    } else {
      var selected_staff_member = staffList.getSelectionModel().getSelectedItems().get(0);
      report_event =
          new ReportEvent(
              selected_clinics, selected_staff_member, report_type, startDate, endDate, null, this);
    }
    EventBus.getDefault().post(report_event);
  }

  /**
   * Sets split pane with report pane (occurs after report request
   * @param reportPane Pane to be set
   */
  public void setViewedReport(Pane reportPane) {
    this.splitPane.getItems().set(2, reportPane);
  }

  static class ReportTypeComboBoxItem {
    public final ReportType reportType;
    public final String reportTypeName;

    public ReportTypeComboBoxItem(ReportType reportType, String reportTypeName) {
      this.reportType = reportType;
      this.reportTypeName = reportTypeName;
    }

    /**
     * Override of String.toString
     * @return The report type name
     */
    @Override
    public String toString() {
      return this.reportTypeName;
    }
  }
}
