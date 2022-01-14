package il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;

public class ChooseAppointmentTypeViewController extends ViewController {
  private final Patient patient;

  @FXML public Label clinicNameLabel;

  @FXML public Accordion accordion;

  public ChooseAppointmentTypeViewController(Patient patient) {
    this.patient = patient;
  }

  @FXML
  public void initialize() {
    accordion.setExpandedPane(accordion.getPanes().get(0));

    clinicNameLabel.setText(patient.getHome_clinic().getName());
  }

  private boolean patientIsMinor() {
    return patient.getBirthday().isAfter(LocalDateTime.now().minusYears(18));
  }

  @FXML
  public void setAppointmentWithGP(ActionEvent event) {
    SetAppointmentEvent apptEvent = new SetAppointmentEvent(this, null, patient, null);
    apptEvent.appointmentType =
        new AppointmentType(patientIsMinor() ? "Pediatrician" : "Family Doctor");
    EventBus.getDefault().post(apptEvent);
  }
}
