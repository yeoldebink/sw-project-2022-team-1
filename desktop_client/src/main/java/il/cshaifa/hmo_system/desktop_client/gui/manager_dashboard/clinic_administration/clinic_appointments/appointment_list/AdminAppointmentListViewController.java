package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.Utils;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.desktop_client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.desktop_client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;

public class AdminAppointmentListViewController extends ViewController {

  @FXML private TableView<AppointmentForAdminTableView> appt_table;

  @FXML private TableColumn<AppointmentForAdminTableView, String> appt_type;
  @FXML private TableColumn<AppointmentForAdminTableView, String> appt_date;
  @FXML private TableColumn<AppointmentForAdminTableView, LocalDateTime> called_time;
  @FXML private TableColumn<AppointmentForAdminTableView, String> comments;
  @FXML private TableColumn<AppointmentForAdminTableView, Boolean> taken;
  @FXML private TableColumn<AppointmentForAdminTableView, String> patient_assigned;

  @FXML private Label staff_member_name;

  @FXML private Button addAppointmentsButton;

  public final User staff_member;
  private ArrayList<Appointment> appt_list = null;

  public AdminAppointmentListViewController(User staff_member) {
    this.staff_member = staff_member;
  }

  @FXML
  public void initialize() {
    this.staff_member_name.setText(staff_member.getFirstName() + " " + staff_member.getLastName());
    appt_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    setCellValueFactory();

    if (!(staff_member.getRole().getName().equals("Family Doctor")
        || staff_member.getRole().getName().equals("Pediatrician")
        || staff_member.getRole().isSpecialist())) {
      addAppointmentsButton.setDisable(true);
    }
  }

  @FXML
  void deleteSelectedAppointments(ActionEvent event) {
    var appts_selected =
        new ArrayList<AppointmentForAdminTableView>(
            appt_table.getSelectionModel().getSelectedItems());
    var appts_to_delete = new ArrayList<Appointment>();

    // Iterate over selected AppointmentForTableView, and for each find
    // relevant Appointment object
    for (var appt_selected : appts_selected) {
      for (var appt : appt_list) {
        if (appt.getId() == appt_selected.getId()) {
          appts_to_delete.add(appt);
        }
      }
    }

    EventBus.getDefault()
        .post(new AdminAppointmentListEvent(this.staff_member, appts_to_delete, this));
  }

  @FXML
  void showEditAppointmentDialog(ActionEvent event) {}

  @FXML
  void showAddAppointmentDialog(ActionEvent event) {
    EventBus.getDefault().post(new AddAppointmentEvent(this.staff_member, null, 0, this));
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
    this.appt_list = appt_list;
    this.appt_list.sort(Comparator.comparing(Appointment::getDate));
    ArrayList<AppointmentForAdminTableView> appt_list_table =
        new ArrayList<AppointmentForAdminTableView>();

    for (var appt : appt_list) {
      appt_list_table.add(
          new AppointmentForAdminTableView(
              appt.getId(),
              appt.getType(),
              appt.getDate(),
              appt.getCalled_time(),
              appt.getComments(),
              appt.isTaken(),
              appt.getPatient()));
    }

    appt_table.getItems().setAll(appt_list_table);
  }

  public static class AppointmentForAdminTableView {

    Integer id;
    String type_name;
    String appt_date_str;
    LocalDateTime called_time;
    String comments;
    Boolean taken;
    String patient_name;

    public AppointmentForAdminTableView(
        Integer id,
        AppointmentType type,
        LocalDateTime appt_date,
        LocalDateTime called_time,
        String comments,
        Boolean taken,
        Patient patient) {
      this.id = id;
      this.type_name = type.getName();
      this.appt_date_str = Utils.prettifyDateTime(appt_date);
      this.called_time = called_time;
      this.comments = comments;
      this.taken = taken;
      if (patient != null && taken) { // only display patient name for taken appointments
        this.patient_name = patient.getUser().getFirstName() + " " + patient.getUser().getLastName();
      } else {
        this.patient_name = "";
      }
    }

    public Integer getId() {
      return id;
    }

    public String getAppt_date() {
      return appt_date_str;
    }

    public LocalDateTime getCalled_time() {
      return called_time;
    }

    public String getComments() {
      return comments;
    }

    public Boolean getTaken() {
      return taken;
    }

    public String getPatient_name() {
      return patient_name;
    }

    public String getAppt_type() {
      return type_name;
    }
  }

}
