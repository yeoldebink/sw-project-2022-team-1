package il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.patient_history;

import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.desktop_client.events.PatientAppointmentListEvent;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import org.greenrobot.eventbus.EventBus;

public class PatientAppointmentHistoryListViewController extends ViewController {

  @FXML private TableView<AppointmentForPatientHistoryView> appt_table;

  @FXML private TableColumn<AppointmentForPatientHistoryView, LocalDateTime> appt_date;
  @FXML private TableColumn<AppointmentForPatientHistoryView, String> appt_type_name;
  @FXML private TableColumn<AppointmentForPatientHistoryView, String> role_name;
  @FXML private TableColumn<AppointmentForPatientHistoryView, String> staff_member_name;
  @FXML private TableColumn<AppointmentForPatientHistoryView, String> clinic_name;
  @FXML private TableColumn<AppointmentForPatientHistoryView, String> appt_passed;
  @FXML private MenuItem cancel_menu_item;

  public PatientAppointmentHistoryListViewController(Patient connected_patient) {}

  @FXML
  public void initialize() {
    appt_table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    setCellValueFactory();
  }

  void populateAppointmentsTable(ArrayList<Appointment> appt_list) {

    ArrayList<AppointmentForPatientHistoryView> appts_to_populate =
        new ArrayList<AppointmentForPatientHistoryView>();

    for (var appt : appt_list) {
      appts_to_populate.add(new AppointmentForPatientHistoryView(appt));
    }

    appts_to_populate.sort(
        Comparator.comparing(AppointmentForPatientHistoryView::getAppt_date).reversed());

    appt_table.getItems().setAll(appts_to_populate);
  }

  @FXML
  void requestCancelAppointments(ActionEvent actionEvent) {
    var appt = appt_table.getSelectionModel().getSelectedItems().get(0).appt;
    var appt_cancel_list = new ArrayList<Appointment>();
    appt_cancel_list.add(appt);

    EventBus.getDefault().post(new PatientAppointmentListEvent(appt_cancel_list, this));
  }

  @FXML
  void contextMenuRequested(ContextMenuEvent contextMenuEvent) {
    disableCancelMenuItemIfSelectedPassedAppointment();
  }

  private void disableCancelMenuItemIfSelectedPassedAppointment() {
    var selected_appt_rows =
        new ArrayList<AppointmentForPatientHistoryView>(
            appt_table.getSelectionModel().getSelectedItems());

    for (var appt : selected_appt_rows) {
      if (appt.appt_passed.equals("Yes")) {
        cancel_menu_item.setDisable(true);
        return;
      }
    }

    cancel_menu_item.setDisable(false);
  }

  void setCellValueFactory() {
    appt_date.setCellValueFactory((new PropertyValueFactory<>("Appt_date")));
    appt_type_name.setCellValueFactory((new PropertyValueFactory<>("Appt_type_name")));
    role_name.setCellValueFactory((new PropertyValueFactory<>("Role_name")));
    staff_member_name.setCellValueFactory((new PropertyValueFactory<>("Staff_member_name")));
    clinic_name.setCellValueFactory((new PropertyValueFactory<>("Clinic_name")));
    appt_passed.setCellValueFactory((new PropertyValueFactory<>("Appt_passed")));
  }

  public static class AppointmentForPatientHistoryView {
    private final Integer id;
    private final LocalDateTime appt_date;
    private final String appt_type_name;
    private final String role_name;
    private final String staff_member_name;
    private final String clinic_name;
    private final String appt_passed;
    public final Appointment appt;

    public AppointmentForPatientHistoryView(Appointment appointment) {
      var staff_member = appointment.getStaff_member();

      this.id = appointment.getId();
      this.appt_date = appointment.getDate();
      this.appt_type_name = appointment.getType().getName();

      if (staff_member != null) {
        this.role_name = appointment.getStaff_member().getRole().getName();
        this.staff_member_name = staff_member.toString();
      } else {
        this.role_name = "";
        this.staff_member_name = "";
      }

      this.clinic_name = appointment.getClinic().getName();
      this.appt_passed = LocalDateTime.now().isAfter(appointment.getDate()) ? "Yes" : "No";
      this.appt = appointment;
    }

    public Integer getId() {
      return id;
    }

    public LocalDateTime getAppt_date() {
      return appt_date;
    }

    public String getAppt_type_name() {
      return appt_type_name;
    }

    public String getRole_name() {
      return role_name;
    }

    public String getStaff_member_name() {
      return staff_member_name;
    }

    public String getClinic_name() {
      return clinic_name;
    }

    public String getAppt_passed() {
      return appt_passed;
    }
  }
}
