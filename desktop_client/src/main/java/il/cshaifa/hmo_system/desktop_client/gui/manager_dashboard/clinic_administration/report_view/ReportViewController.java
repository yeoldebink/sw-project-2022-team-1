package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import java.time.Duration;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReportViewController extends ViewController {
  @FXML private TableView<ReportDataRow> report_table;
  @FXML private Label report_type_label;
  @FXML private TableColumn<ReportDataRow, String> keyColumn;
  @FXML private TableColumn<ReportDataRow, String> valueColumn;

  private final ReportType report_type;

  public ReportViewController(ReportType report_type) {
    this.report_type = report_type;
  }

  @FXML
  public void initialize() {
    setCellValueFactory();

    processReportType();
  }

  /**
   * Sets view text and table view column text by report type
   */
  private void processReportType() {
    switch (this.report_type) {
      case MISSED_APPOINTMENTS:
        this.setViewText("Missed Appointments", "Type", "Amount missed");
        break;
      case AVERAGE_WAIT_TIMES:
        this.setViewText("Average Wait Times", "Staff member", "Wait time");
        break;
      case APPOINTMENT_ATTENDANCE:
        this.setViewText("Appointment Attendance", "Type", "Num. attended");
        break;
      default:
        throw new IllegalStateException("Unexpected report type: " + this.report_type);
    }
  }

  private void setViewText(
      String report_type_label_text, String key_column_text, String value_column_text) {
    this.report_type_label.setText(report_type_label_text);
    this.keyColumn.setText(key_column_text);
    this.valueColumn.setText(value_column_text);
  }

  void populateReportTable(Map<?, Integer> report_data) {
    if (report_type == ReportType.AVERAGE_WAIT_TIMES) {
      for (var entry : report_data.entrySet()) {
        report_table.getItems().add(new ReportDataRow((User) entry.getKey(), entry.getValue()));
      }
    } else {
      for (var entry : report_data.entrySet()) {
        report_table.getItems().add(new ReportDataRow(entry.getKey().toString(), entry.getValue()));
      }
    }
  }

  private void setCellValueFactory() {
    keyColumn.setCellValueFactory((new PropertyValueFactory<>("Key")));
    valueColumn.setCellValueFactory((new PropertyValueFactory<>("Value")));
  }

  public static class ReportDataRow {

    private final String key;
    private final String value;

    // appt types report
    public ReportDataRow(String appt_type, Integer value) {
      this.key = appt_type;
      this.value = value.toString();
    }

    // avg wait times
    public ReportDataRow(User staff_member, Integer value) {
      this.key = staff_member.toString();
      var waitTime = Duration.ofSeconds(value);
      this.value =
          String.format(
              "%dh %dm %ds",
              waitTime.toHoursPart(), waitTime.toMinutesPart(), waitTime.toSecondsPart());
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }
  }
}
