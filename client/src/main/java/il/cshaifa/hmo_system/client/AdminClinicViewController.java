package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.EditClinicEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;

public class AdminClinicViewController extends ViewController {

  private final Clinic clinic;

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
  public void initialize() {
    sunHoursTextField.setText(clinic.getSun_hours());
    monHoursTextField.setText(clinic.getMon_hours());
    tueHoursTextField.setText(clinic.getTue_hours());
    wedHoursTextField.setText(clinic.getWed_hours());
    thuHoursTextField.setText(clinic.getThu_hours());
    friHoursTextField.setText(clinic.getFri_hours());
    satHoursTextField.setText(clinic.getSat_hours());
  }

  @FXML
  public void requestClinicUpdate(ActionEvent actionEvent) {
    clinic.setSun_hours(sunHoursTextField.getText());
    clinic.setMon_hours(monHoursTextField.getText());
    clinic.setTue_hours(tueHoursTextField.getText());
    clinic.setWed_hours(wedHoursTextField.getText());
    clinic.setThu_hours(thuHoursTextField.getText());
    clinic.setFri_hours(friHoursTextField.getText());
    clinic.setSat_hours(satHoursTextField.getText());

    EventBus.getDefault().post(new EditClinicEvent(this.clinic));

    closeWindow(actionEvent);
  }
}
