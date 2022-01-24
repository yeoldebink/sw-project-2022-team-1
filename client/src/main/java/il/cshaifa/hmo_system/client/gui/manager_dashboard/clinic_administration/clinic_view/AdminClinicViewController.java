package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_view;

import il.cshaifa.hmo_system.client.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

public class AdminClinicViewController extends RoleDefinedViewController {

  private final Clinic clinicCopy;
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
    super(role);
    this.clinicCopy = new Clinic(clinic); // prevents saves for invalid objects
  }

  @FXML
  public void initialize() {
    name.setText(clinicCopy.getName());
    address.setText(clinicCopy.getAddress());
    manager.setText(
        clinicCopy.getManager_user().getFirstName()
            + " "
            + clinicCopy.getManager_user().getLastName());
    sunHoursTextField.setText(clinicCopy.getSun_hours());
    monHoursTextField.setText(clinicCopy.getMon_hours());
    tueHoursTextField.setText(clinicCopy.getTue_hours());
    wedHoursTextField.setText(clinicCopy.getWed_hours());
    thuHoursTextField.setText(clinicCopy.getThu_hours());
    friHoursTextField.setText(clinicCopy.getFri_hours());
    satHoursTextField.setText(clinicCopy.getSat_hours());

    applyRoleBehavior();
  }

  protected void applyRoleBehavior() {
    if (role.getName().equals("HMO Manager")) {
      sunHoursTextField.setDisable(true);
      monHoursTextField.setDisable(true);
      tueHoursTextField.setDisable(true);
      wedHoursTextField.setDisable(true);
      thuHoursTextField.setDisable(true);
      friHoursTextField.setDisable(true);
      satHoursTextField.setDisable(true);
    } else {
      name.setDisable(true);
      address.setDisable(true);
    }
  }

  @FXML
  public void requestClinicUpdate(ActionEvent actionEvent) {
    clinicCopy.setSun_hours(sunHoursTextField.getText());
    clinicCopy.setMon_hours(monHoursTextField.getText());
    clinicCopy.setTue_hours(tueHoursTextField.getText());
    clinicCopy.setWed_hours(wedHoursTextField.getText());
    clinicCopy.setThu_hours(thuHoursTextField.getText());
    clinicCopy.setFri_hours(friHoursTextField.getText());
    clinicCopy.setSat_hours(satHoursTextField.getText());
    clinicCopy.setName(name.getText());
    clinicCopy.setAddress(address.getText());

    EventBus.getDefault().post(new ClinicEvent(this.clinicCopy, this));
  }
}
