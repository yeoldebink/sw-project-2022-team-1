package il.cshaifa.hmo_system.client.gui.clinic_administration;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.EditClinicEvent;
import il.cshaifa.hmo_system.client.events.EditClinicEvent.Phase;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

public class AdminClinicViewController extends ViewController {

  private final Role role;

  private final Clinic clinic;
  @FXML private TextField name;
  @FXML private TextField address;
  @FXML private Label manager;

  @FXML private TextField sunHoursTextField;
  @FXML private TextField monHoursTextField;
  @FXML private TextField tueHoursTextField;
  @FXML private TextField wedHoursTextField;
  @FXML private TextField thuHoursTextField;
  @FXML private TextField friHoursTextField;
  @FXML private TextField satHoursTextField;

  public AdminClinicViewController(Clinic clinic, Role role) {
    this.clinic = clinic;
    this.role = role;
  }

  @FXML
  public void initialize() {
    name.setText(clinic.getName());
    address.setText(clinic.getAddress());
    manager.setText(
        clinic.getManager_user().getFirstName() + clinic.getManager_user().getLastName());
    sunHoursTextField.setText(clinic.getSun_hours());
    monHoursTextField.setText(clinic.getMon_hours());
    tueHoursTextField.setText(clinic.getTue_hours());
    wedHoursTextField.setText(clinic.getWed_hours());
    thuHoursTextField.setText(clinic.getThu_hours());
    friHoursTextField.setText(clinic.getFri_hours());
    satHoursTextField.setText(clinic.getSat_hours());

    if (role.getName().equals("HMO Manager")) {
      sunHoursTextField.setEditable(false);
      monHoursTextField.setEditable(false);
      tueHoursTextField.setEditable(false);
      wedHoursTextField.setEditable(false);
      thuHoursTextField.setEditable(false);
      friHoursTextField.setEditable(false);
      satHoursTextField.setEditable(false);
    } else {
      name.setEditable(false);
      address.setEditable(false);
    }
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

    EventBus.getDefault().post(new EditClinicEvent(this.clinic, Phase.SEND));

    closeWindow(actionEvent);
  }
}
