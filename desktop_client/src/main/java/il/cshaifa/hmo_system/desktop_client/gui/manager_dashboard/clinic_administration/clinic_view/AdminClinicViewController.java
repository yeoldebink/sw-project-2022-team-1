package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_view;

import il.cshaifa.hmo_system.client_base.base_controllers.RoleDefinedViewController;
import il.cshaifa.hmo_system.client_base.events.ClinicEvent;
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

  /**
   * Sets view data and applies view role behavior
   */
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

  /**
   * Emits an event requesting the Clinic entity in the event be updated in the database
   * @param actionEvent
   */
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
