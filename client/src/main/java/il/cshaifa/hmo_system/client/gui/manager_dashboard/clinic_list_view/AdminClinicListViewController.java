package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_list_view;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;

public class AdminClinicListViewController extends ViewController {

  @FXML public TableColumn<Clinic, Integer> id;
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
    id.setCellValueFactory(new PropertyValueFactory<>("Id"));
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

  @FXML
  void showEditClinicDialog(ActionEvent event) {
    var clinic = clinicTable.getSelectionModel().getSelectedItem();
    EventBus.getDefault().post(new ClinicEvent(clinic, ClinicEvent.Phase.EDIT));
  }

  void populateClinicTable(ArrayList<Clinic> clinics) {
    // fill the table
    clinicTable.getItems().setAll(clinics);
  }
}
