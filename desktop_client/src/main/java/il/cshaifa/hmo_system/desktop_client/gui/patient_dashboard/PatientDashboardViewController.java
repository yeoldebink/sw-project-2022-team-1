package il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.events.AppointmentListEvent;
import il.cshaifa.hmo_system.desktop_client.events.GreenPassStatusEvent;
import il.cshaifa.hmo_system.desktop_client.events.MyClinicEvent;
import il.cshaifa.hmo_system.desktop_client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;

public class PatientDashboardViewController extends ViewController {

  private final Patient patient;

  @FXML private Label nameLabel;
  @FXML private Label nextAppointmentStatusLabel;
  @FXML private Label nextAppointmentDataLabel;

  public PatientDashboardViewController(Patient patient) {
    this.patient = patient;
  }

  // TODO: welcome text:

  /** Green pass status (?) */
  @FXML
  public void initialize() {
    nameLabel.setText(
        String.format(
            "Welcome, %s", patient.getUser().toString()));
    nextAppointmentStatusLabel.setText("Your next appointment:");
  }

  /**
   * Emits event requesting appointment list view to open
   * @param event
   */
  @FXML
  public void viewPatientAppointments(ActionEvent event) {
    EventBus.getDefault().post(new AppointmentListEvent(null, this));
  }

  /**
   * Emits event requesting the set appointment view to open
   * @param event
   */
  @FXML
  public void setAppointment(ActionEvent event) {
    // no data, just asking for the window to be opened
    EventBus.getDefault().post(new SetAppointmentEvent(this, null, null, null));
  }

  /**
   * Updates the view with information on connected user's upcoming appointment, if exists
   * @param appointment User's upcoming appointment (or null)
   */
  public void updateNextAppointmentInfo(Appointment appointment) {
    if (appointment == null) {
      nextAppointmentStatusLabel.setText("You have no upcoming appointments");
      nextAppointmentDataLabel.setText("");
    } else {
      var date = appointment.getDate();

      String apptTypeInfo;

      if (appointment.getStaff_member() != null) {
        apptTypeInfo =
            String.format(
                "Dr. %s, %s\n",
                appointment.getStaff_member().toString(),
                appointment.getStaff_member().getRole().getName());
      } else {
        apptTypeInfo = appointment.getType().getName() + "\n";
      }

      nextAppointmentStatusLabel.setText("Your next appointment:");
      // Location: clinic name, address
      // Dr. Staff Member, role
      // February 19th, 2022 at 9:45
      nextAppointmentDataLabel.setText(
          String.format(
              "Location: %s, %s\n%s%s, %s %s at %02d:%02d",
              appointment.getClinic().getName(),
              appointment.getClinic().getAddress(),
              apptTypeInfo,
              date.getDayOfWeek(),
              date.getMonth(),
              date.getDayOfMonth(),
              date.getHour(),
              date.getMinute()));
    }
  }

  /**
   * Emits event requesting the my clinic view to open
   * @param event
   */
  @FXML
  public void viewMyClinic(ActionEvent event) {
    EventBus.getDefault().post(new MyClinicEvent(this));
  }

  /**
   * Emits event requesting the my green pass view to open
   * @param event
   */
  @FXML
  public void viewMyGreenPass(ActionEvent event) {
    EventBus.getDefault().post(new GreenPassStatusEvent(this));
  }
}
