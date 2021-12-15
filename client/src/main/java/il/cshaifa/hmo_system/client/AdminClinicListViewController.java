package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.entities.Clinic;
import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AdminClinicListViewController {

  private ArrayList<Clinic> clinics;

  public AdminClinicListViewController() throws IOException {
    EventBus.getDefault().register(this);
    HMOClient.getClient().getClinics();
  }

  @FXML private TableView<?> clinicTable;

  public void setClinics(ArrayList<Clinic> clinics) {
    this.clinics = clinics;
  }

  @FXML
  void showAddClinicDialog(ActionEvent event) {}

  @FXML
  void showEditClinicDialog(ActionEvent event) {}

  @Subscribe
  public void onResponse(ResponseEvent responseEvent) {
    System.out.println("Got a clinic list!");
  }
}
