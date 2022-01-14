package il.cshaifa.hmo_system.client.gui.patient_dashboard.patient_history;


import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.PatientAppointmentListEvent;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;

import il.cshaifa.hmo_system.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import org.greenrobot.eventbus.EventBus;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PatientAppointmentHistoryListViewController extends ViewController {

    @FXML
    private TableView<AppointmentForPatientHistoryView> appt_table;

    @FXML
    private Label patient_name;

    @FXML
    private TableColumn<AppointmentForPatientHistoryView, LocalDateTime> appt_date;
    @FXML
    private TableColumn<AppointmentForPatientHistoryView, String> appt_type_name;
    @FXML
    private TableColumn<AppointmentForPatientHistoryView, String> specialist_role_name;
    @FXML
    private TableColumn<AppointmentForPatientHistoryView, String> staff_member_name;
    @FXML
    private TableColumn<AppointmentForPatientHistoryView, String> clinic_name;
    @FXML
    private TableColumn<AppointmentForPatientHistoryView, String> appt_passed;

    @FXML
    private ContextMenu context_menu;
    @FXML
    private MenuItem cancel_menu_item;


    private final Patient patient;
    private ArrayList<Appointment> appt_list = null;

    public PatientAppointmentHistoryListViewController(Patient connected_patient) {
        this.patient = connected_patient;
    }

    @FXML
    public void initialize() {
        this.patient_name.setText(patient.getUser().getFirstName() + " " + patient.getUser().getLastName());
        appt_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setCellValueFactory();
    }

    void populateAppointmentsTable(ArrayList<Appointment> appt_list) {
        this.appt_list = appt_list;

        ArrayList<AppointmentForPatientHistoryView> appts_to_populate = new ArrayList<AppointmentForPatientHistoryView>();

        for (var appt : appt_list) {
            appts_to_populate.add(new AppointmentForPatientHistoryView(appt));
        }

        appt_table.getItems().setAll(appts_to_populate);
    }

    @FXML
    void requestCancelAppointments(ActionEvent actionEvent) {
        var appts_selected =
                new ArrayList<AppointmentForPatientHistoryView>(appt_table.getSelectionModel().getSelectedItems());
        var appts_to_cancel = new ArrayList<Appointment>();

        for (var appt_selected : appts_selected) {
            for (var appt : appt_list) {
                if (appt.getId() == appt_selected.getId()) {
                    appts_to_cancel.add(appt);
                }
            }
        }

        EventBus.getDefault()
                .post(new PatientAppointmentListEvent(appts_to_cancel, this));
    }

    @FXML
    void contextMenuRequested(ContextMenuEvent contextMenuEvent) {
        disableCancelMenuItemIfSelectedPassedAppointment();
    }

    private void disableCancelMenuItemIfSelectedPassedAppointment() {
        var selected_appt_rows = new ArrayList<AppointmentForPatientHistoryView>(appt_table.getSelectionModel().getSelectedItems());

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
        specialist_role_name.setCellValueFactory((new PropertyValueFactory<>("Role_name")));

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

        public AppointmentForPatientHistoryView(Appointment appointment) {
            var staff_member = appointment.getStaff_member();

            this.id = appointment.getId();
            this.appt_date = appointment.getDate();
            this.appt_type_name = appointment.getType().getName();
            this.role_name = appointment.getStaff_member().getRole().getName();
            this.staff_member_name = staff_member.getFirstName() + " " + staff_member.getLastName();
            this.clinic_name = appointment.getClinic().getName();
            this.appt_passed = LocalDateTime.now().isAfter(appointment.getDate()) ? "Yes" : "No";
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

