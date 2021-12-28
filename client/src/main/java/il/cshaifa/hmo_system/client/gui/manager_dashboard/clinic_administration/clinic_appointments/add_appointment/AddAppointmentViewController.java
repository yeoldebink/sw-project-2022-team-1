package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent.Phase;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;

public class AddAppointmentViewController extends ViewController {
  private final User staff_member;
  private final Clinic clinic;

  @FXML private TextField num_appts;
  @FXML private DatePicker start_date;
  @FXML private TextField start_time;
  @FXML private Label error_text;
  @FXML private Button create_appts;
  @FXML private Label staff_member_name;

  public AddAppointmentViewController(User staff_member, Clinic clinic) {
    this.staff_member = staff_member;
    this.clinic = clinic;
  }

  public void initialize() {
    this.staff_member_name.setText(staff_member.getFirstName() + " " + staff_member.getLastName());
  }

  @FXML
  void requestCreateAppointments(ActionEvent actionEvent) {
    // Disable button to prevent spam
    error_text.setText("");
    create_appts.setDisable(true);

    try {
      Time time_value =
          new Time(new SimpleDateFormat("HH:mm").parse(start_time.getText()).getTime());
      LocalDateTime start_datetime =
          LocalDateTime.of(start_date.getValue(), time_value.toLocalTime());
      Integer count_appointments = Integer.parseInt((num_appts.getText()));

      EventBus.getDefault()
          .post(
              new AddAppointmentEvent(
                  this.staff_member, this.clinic, start_datetime, count_appointments, Phase.SEND));

      closeWindow(actionEvent);
    } catch (ParseException e) {
      e.printStackTrace();
      error_text.setText("Invalid time format");
      create_appts.setDisable(false);
    }
  }
}
