package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.entities.Clinic;
import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class AdminClinicListViewController extends ViewController {

  private ArrayList<Clinic> clinics;
  @FXML private TableView<?> clinicTable;

  public AdminClinicListViewController() throws IOException {
    HMOClient.getClient().getClinics();
  }

  @FXML
  void showAddClinicDialog(ActionEvent event) {}

  @FXML
  void showEditClinicDialog(ActionEvent event) {}

  void populateClinicTable(ArrayList<Clinic> clinics) {}
}
