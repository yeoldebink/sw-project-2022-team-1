package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.add_appointment;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent.Phase;
import il.cshaifa.hmo_system.entities.User;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

public class AddDoctorAppointmentsViewController extends ViewController {
  private final User staff_member;

  @FXML private TextField num_appts;
  @FXML private DatePicker start_date;
  @FXML private TextField start_time;
  @FXML private Label error_text;
  @FXML private Button create_appts;
  @FXML private Label staff_member_name;

  public AddDoctorAppointmentsViewController(User staff_member) {
    this.staff_member = staff_member;
  }

  public void initialize() {
    this.start_date.getEditor().setDisable(true); // Block from writing text into the DatePicker
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

      Integer count_appointments = Integer.parseInt(num_appts.getText());

      EventBus.getDefault()
          .post(
              new AddAppointmentEvent(
                  this.staff_member, start_datetime, count_appointments, Phase.SEND));

    } catch (ParseException e) {
      e.printStackTrace();
      setErrorMessage("Invalid time format");
    } catch (NumberFormatException e){
      e.printStackTrace();
      setErrorMessage("# appointment cannot be empty");
    } catch (NullPointerException e){
      e.printStackTrace();
      setErrorMessage("Missing Date");
    }
  }

  public void setErrorMessage(String message){
    error_text.setText(message);
    create_appts.setDisable(false);
  }
}
