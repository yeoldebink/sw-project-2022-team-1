package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_list_view;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.events.ClinicEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;

public class AdminClinicListViewController extends ViewController {

  @FXML public TableColumn<Clinic, String> name;
  @FXML public TableColumn<Clinic, String> address;
  @FXML public TableColumn<Clinic, String> sun_hours;
  @FXML public TableColumn<Clinic, String> mon_hours;
  @FXML public TableColumn<Clinic, String> tue_hours;
  @FXML public TableColumn<Clinic, String> wed_hours;
  @FXML public TableColumn<Clinic, String> thu_hours;
  @FXML public TableColumn<Clinic, String> fri_hours;
  @FXML public TableColumn<Clinic, String> sat_hours;

  @FXML private TableView<Clinic> clinicTable;

  @FXML
  public void initialize() {
    setCellValueFactory();
  }

  /** Binds table column values to class getters */
  void setCellValueFactory() {
    name.setCellValueFactory(new PropertyValueFactory<>("Name"));
    address.setCellValueFactory(new PropertyValueFactory<>("Address"));
    sun_hours.setCellValueFactory(new PropertyValueFactory<>("Sun_hours"));
    mon_hours.setCellValueFactory(new PropertyValueFactory<>("Mon_hours"));
    tue_hours.setCellValueFactory(new PropertyValueFactory<>("Tue_hours"));
    wed_hours.setCellValueFactory(new PropertyValueFactory<>("Wed_hours"));
    thu_hours.setCellValueFactory(new PropertyValueFactory<>("Thu_hours"));
    fri_hours.setCellValueFactory(new PropertyValueFactory<>("Fri_hours"));
    sat_hours.setCellValueFactory(new PropertyValueFactory<>("Sat_hours"));
  }

  @FXML
  void showAddClinicDialog(ActionEvent event) {}

  /**
   * Emits event to open the edit clinic dialog of the clinic selected in the view
   *
   * @param event
   */
  @FXML
  void showEditClinicDialog(ActionEvent event) {
    var clinic = clinicTable.getSelectionModel().getSelectedItem();
    EventBus.getDefault().post(new ClinicEvent(clinic, this));
  }

  /**
   * Populates clinic table view with clinics
   *
   * @param clinics Clinics to populate the table view
   */
  void populateClinicTable(ArrayList<Clinic> clinics) {
    // fill the table
    clinicTable.getItems().setAll(clinics);
  }
}
