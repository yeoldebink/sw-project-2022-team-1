package il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.clinic_view;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.entities.Clinic;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MyClinicViewController extends ViewController {

  private final Clinic clinic;

  @FXML private Label clinicName;
  @FXML private Label clinicAddress;
  @FXML private Label managerName;
  @FXML private Label sunHours;
  @FXML private Label monHours;
  @FXML private Label tueHours;
  @FXML private Label wedHours;
  @FXML private Label thuHours;
  @FXML private Label friHours;
  @FXML private Label satHours;

  public MyClinicViewController(Clinic clinic) {
    this.clinic = clinic;
  }

  /**
   * Sets view data
   */
  @FXML
  public void initialize() {
    clinicName.setText(clinic.getName());
    managerName.setText(clinic.getManager_user().toString());
    clinicAddress.setText(clinic.getAddress());

    sunHours.setText(clinic.getSun_hours());
    monHours.setText(clinic.getMon_hours());
    tueHours.setText(clinic.getTue_hours());
    wedHours.setText(clinic.getWed_hours());
    thuHours.setText(clinic.getThu_hours());
    friHours.setText(clinic.getFri_hours());
    satHours.setText(clinic.getSat_hours());
  }
}
