package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.events.EditClinicEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

public class AdminClinicViewController {

  private Clinic clinic;

  @FXML private TextField sunHoursTextField;
  @FXML private TextField monHoursTextField;
  @FXML private TextField tueHoursTextField;
  @FXML private TextField wedHoursTextField;
  @FXML private TextField thuHoursTextField;
  @FXML private TextField friHoursTextField;
  @FXML private TextField satHoursTextField;

  public AdminClinicViewController(Clinic clinic) {
    this.clinic = clinic;
  }

  @FXML
  public void requestClinicUpdate(ActionEvent actionEvent) {
    EventBus.getDefault().post(new EditClinicEvent(this.clinic));
  }
}
