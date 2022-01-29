package il.cshaifa.hmo_system.on_site_client.gui.staff;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.utils.Utils;
import il.cshaifa.hmo_system.on_site_client.events.ViewAppointmentEvent;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.greenrobot.eventbus.EventBus;

public class StaffAppointmentViewController extends ViewController {
  private final QueuedAppointment q_appt;
  private final boolean readonly;

  @FXML private Label appointmentDetailsLabel;
  @FXML private Label patientNameLabel;
  @FXML private Label appointmentTimeLabel;
  @FXML private Label treatmentTypeLabel;
  @FXML private Label clinicNameLabel;
  @FXML private TextArea commentsTextArea;

  @FXML private Button updateCommentsButton;

  public StaffAppointmentViewController(QueuedAppointment q_appt, boolean readonly) {
    this.q_appt = q_appt;
    this.readonly = readonly;
  }

  @FXML public void initialize() {
    appointmentDetailsLabel.setText(String.format("Appointment Details [number %s]", q_appt.place_in_line));
    patientNameLabel.setText(q_appt.appointment.getPatient().getUser().toString());
    appointmentTimeLabel.setText(Utils.prettifyDateTime(q_appt.appointment.getDate()));
    treatmentTypeLabel.setText(q_appt.appointment.getType().getName());
    clinicNameLabel.setText(q_appt.appointment.getClinic().getName());
    commentsTextArea.setText(q_appt.appointment.getComments());

    updateCommentsButton.setOnAction(actionEvent -> {
      q_appt.appointment.setComments(commentsTextArea.getText());
      EventBus.getDefault().post(new ViewAppointmentEvent(q_appt, this));
    });

    commentsTextArea.setWrapText(true);

    if (readonly) setReadOnly();
  }

  public void setReadOnly() {
    commentsTextArea.setDisable(true);
    updateCommentsButton.setDisable(true);
  }
}
