package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_appointments.appointment_list;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent.Phase;
import il.cshaifa.hmo_system.entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AppointmentListViewController extends ViewController {

    @FXML
    private TableView<AppointmentForTableView> appt_table;

    @FXML
    private TableColumn<AppointmentForTableView, String> appt_type;
    @FXML
    private TableColumn<AppointmentForTableView, LocalDateTime> appt_date;
    @FXML
    private TableColumn<AppointmentForTableView, LocalDateTime> called_time;
    @FXML
    private TableColumn<AppointmentForTableView, String> comments;
    @FXML
    private TableColumn<AppointmentForTableView, Boolean> taken;
    @FXML
    private TableColumn<AppointmentForTableView, String> patient_assigned;

    @FXML
    private Label staff_member_name;

    private final User staff_member;
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
    void deleteSelectedAppointments(ActionEvent event) {
    }

    @FXML
    void showEditAppointmentDialog(ActionEvent event) {
    }

    @FXML
    void showAddAppointmentDialog(ActionEvent event) {
        EventBus.getDefault().post(new AddAppointmentEvent(this.staff_member, this.clinic, null, 0, Phase.OPEN_WINDOW));
    }

    void setCellValueFactory() {
        appt_type.setCellValueFactory((new PropertyValueFactory<>("Appt_type")));
        appt_date.setCellValueFactory((new PropertyValueFactory<>("Appt_date")));
        called_time.setCellValueFactory((new PropertyValueFactory<>("Called_time")));
        comments.setCellValueFactory((new PropertyValueFactory<>("Comments")));
        taken.setCellValueFactory((new PropertyValueFactory<>("Taken")));
        patient_assigned.setCellValueFactory((new PropertyValueFactory<>("Patient_assigned")));
    }

    void populateAppointmentsTable(ArrayList<Appointment> appt_list) {
        ArrayList<AppointmentForTableView> appt_list_table = new ArrayList<AppointmentForTableView>();

        for (var appt : appt_list) {
            appt_list_table.add(
                    new AppointmentForTableView(appt.getType(), appt.getDate(), appt.getCalled_time(),
                            appt.getComments(), appt.isTaken(), appt.getPatient())
            );
        }

        appt_table.getItems().setAll(appt_list_table);
    }

}

class AppointmentForTableView {
    String type_name;
    LocalDateTime appt_date;
    LocalDateTime called_time;
    String comments;
    Boolean taken;
    String patient_name;

    public AppointmentForTableView(AppointmentType type, LocalDateTime appt_date, LocalDateTime called_time,
                                   String comments, Boolean taken, Patient patient) {
        this.type_name = type.getName();
        this.appt_date = appt_date;
        this.called_time = called_time;
        this.comments = comments;
        this.taken = taken;
        this.patient_name = patient.getUser().getFirstName() + " " + patient.getUser().getLastName();
    }

    public String getType_name() {
        return type_name;
    }

    public LocalDateTime getAppt_date() {
        return appt_date;
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


}