package il.cshaifa.hmo_system.on_site_client.gui.staff_appointment_list;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;

public class StaffAppointmentListViewController {

    @FXML private TableView<AppointmentPatientRow> appt_table;
    @FXML private TableColumn<AppointmentPatientRow, LocalTime> appt_time;
    @FXML private TableColumn<AppointmentPatientRow, String> appt_type_name;
    @FXML private Label current_date;
    @FXML private TableColumn<AppointmentPatientRow, String> patient_home_clinic;
    @FXML private TableColumn<AppointmentPatientRow, String> patient_name;
    @FXML private TableColumn<AppointmentPatientRow, String> place_in_line;
    @FXML private Label staff_member_role_name;

    private final User staff_member;

    public StaffAppointmentListViewController(User staff_member) {
        this.staff_member = staff_member;
    }

    @FXML
    public void initialize() {
        appt_table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        current_date.setText(LocalDateTime.now().toString());
        staff_member_role_name.setText(staff_member.getFirstName() + " " + staff_member.getLastName() + ", " + staff_member.getRole().getName());
        setCellValueFactory();
    }

    @FXML
    void requestDequeuePatient(ActionEvent event) {

    }

    void setCellValueFactory() {
        place_in_line.setCellValueFactory((new PropertyValueFactory<>("Place_in_line")));
        appt_time.setCellValueFactory((new PropertyValueFactory<>("Appt_time")));
        appt_type_name.setCellValueFactory((new PropertyValueFactory<>("Appt_type_name")));
        patient_home_clinic.setCellValueFactory((new PropertyValueFactory<>("Patient_home_clinic")));
        patient_name.setCellValueFactory((new PropertyValueFactory<>("Patient_name")));
    }

    void populateAppointmentsTable(ArrayList<QueuedAppointment> appt_list) {

        ArrayList<AppointmentPatientRow> appts_to_populate =
                new ArrayList<AppointmentPatientRow>();

        for (var appt : appt_list) {
            appts_to_populate.add(new AppointmentPatientRow(appt));
        }

        appts_to_populate.sort(
                Comparator.comparing(AppointmentPatientRow::getPlace_in_line));

        appt_table.getItems().setAll(appts_to_populate);
    }

    public static class AppointmentPatientRow {
        private final String place_in_line;
        private final LocalTime appt_time;
        private final String appt_type_name;
        private final String patient_home_clinic;
        private final String patient_name;

        public AppointmentPatientRow(QueuedAppointment q_app) {
            this.place_in_line = q_app.place_in_line;
            this.appt_time = q_app.appointment.getDate().toLocalTime();
            this.appt_type_name = q_app.appointment.getType().getName();
            this.patient_home_clinic = q_app.appointment.getPatient().getHome_clinic().getName();
            this.patient_name = q_app.appointment.getPatient().getUser().getFirstName() +
                    " " + q_app.appointment.getPatient().getUser().getLastName();
        }

        public String getPlace_in_line() {
            return place_in_line;
        }

        public LocalTime getAppt_time() {
            return appt_time;
        }

        public String getAppt_type_name() {
            return appt_type_name;
        }

        public String getPatient_home_clinic() {
            return patient_home_clinic;
        }

        public String getPatient_name() {
            return patient_name;
        }
    }
}
