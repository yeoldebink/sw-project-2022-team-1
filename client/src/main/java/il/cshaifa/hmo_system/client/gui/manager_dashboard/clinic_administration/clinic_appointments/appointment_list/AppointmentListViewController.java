package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent.Phase;
import il.cshaifa.hmo_system.entities.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;

public class AppointmentListViewController extends ViewController {

  @FXML private TableView<AppointmentForTableView> appt_table;

  @FXML private TableColumn<AppointmentForTableView, String> appt_type;
  @FXML private TableColumn<AppointmentForTableView, LocalDateTime> appt_date;
  @FXML private TableColumn<AppointmentForTableView, LocalDateTime> called_time;
  @FXML private TableColumn<AppointmentForTableView, String> comments;
  @FXML private TableColumn<AppointmentForTableView, Boolean> taken;
  @FXML private TableColumn<AppointmentForTableView, String> patient_assigned;

  @FXML private Label staff_member_name;

  public final User staff_member;
  private final Clinic clinic;

  public AppointmentListViewController(User staff_member, Clinic clinic) {
    this.staff_member = staff_member;
    this.clinic = clinic;
  }

  @FXML
  public void initialize() {
    this.staff_member_name.setText(staff_member.getFirstName() + " " + staff_member.getLastName());

    setCellValueFactory();
  }

  @FXML
  void deleteSelectedAppointments(ActionEvent event) {}

  @FXML
  void showEditAppointmentDialog(ActionEvent event) {}

  @FXML
  void showAddAppointmentDialog(ActionEvent event) {
    EventBus.getDefault()
        .post(new AddAppointmentEvent(this.staff_member, null, 0, Phase.OPEN_WINDOW));
  }

  void setCellValueFactory() {
    appt_type.setCellValueFactory((new PropertyValueFactory<>("Appt_type")));
    appt_date.setCellValueFactory((new PropertyValueFactory<>("Appt_date")));
    called_time.setCellValueFactory((new PropertyValueFactory<>("Called_time")));
    comments.setCellValueFactory((new PropertyValueFactory<>("Comments")));
    taken.setCellValueFactory((new PropertyValueFactory<>("Taken")));
    patient_assigned.setCellValueFactory((new PropertyValueFactory<>("Patient_name")));
  }

  void populateAppointmentsTable(ArrayList<Appointment> appt_list) {
    ArrayList<AppointmentForTableView> appt_list_table = new ArrayList<AppointmentForTableView>();

    for (var appt : appt_list) {
      appt_list_table.add(
          new AppointmentForTableView(
              appt.getType(),
              appt.getDate(),
              appt.getCalled_time(),
              appt.getComments(),
              appt.isTaken(),
              appt.getPatient()));
    }

    appt_table.getItems().setAll(appt_list_table);
  }
}
