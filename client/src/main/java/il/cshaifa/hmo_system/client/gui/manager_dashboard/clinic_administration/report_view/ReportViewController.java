package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import java.util.ArrayList;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReportViewController extends ViewController {
  @FXML private TableView<ReportDataRow> report_table;
  @FXML private Label report_type_label;
  @FXML private TableColumn<ReportDataRow, String> key;
  @FXML private TableColumn<ReportDataRow, Double> value;
  @FXML private Button close_button;

  // TODO: How to add type of report to parameters if ReportType is defined in server only
  public ReportViewController() {}

  @FXML
  public void initialize() {
    setCellValueFactory();
    // TODO Tomer: Set view label and table column text according to report type (data can be
    // transparent to view)
  }

  @FXML
  void requestCloseWindow(ActionEvent event) {
    closeWindow(event);
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
