package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;

import java.util.ArrayList;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class ReportViewController extends ViewController {
    @FXML private TableView<ReportDataRow> report_table;
    @FXML private Label report_type_label;
    @FXML private TableColumn<ReportDataRow, String> key;
    @FXML private TableColumn<ReportDataRow, Double> value;

    private ReportType report_type;

    public ReportViewController(ReportType report_type) {
        this.report_type = report_type;
    }

    @FXML
    public void initialize() {
        setCellValueFactory();

        processReportType();
    }

    private void processReportType() {
        switch (this.report_type) {
            case MISSED_APPOINTMENTS -> this.setViewText("Missed Appointments", "Type", "Amount missed");
            case AVERAGE_WAIT_TIMES -> this.setViewText("Average Doctor Wait Times", "Doctor name", "Wait time");
            case APPOINTMENT_ATTENDANCE -> this.setViewText("Appointment Attendance", "Type", "Num. attended");
            default -> throw new IllegalStateException("Unexpected report type: " + this.report_type);
        }
    }

    private void setViewText(String report_type_label_text, String key_column_text, String value_column_text) {
        this.report_type_label.setText(report_type_label_text);
        this.key.setText(key_column_text);
        this.value.setText(value_column_text);
    }

    void populateReportTable(Map<String, Double> report_data) {

        ArrayList<ReportDataRow> report_data_rows = new ArrayList<ReportDataRow>();

        for (Map.Entry<String, Double> entry : report_data.entrySet()) {
            report_data_rows.add(new ReportDataRow(entry.getKey(), entry.getValue()));
        }

        report_table.getItems().setAll(report_data_rows);
    }

    private void setCellValueFactory() {
        key.setCellValueFactory((new PropertyValueFactory<>("Key")));
        value.setCellValueFactory((new PropertyValueFactory<>("Value")));
    }
}
