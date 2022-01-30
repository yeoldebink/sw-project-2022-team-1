package il.cshaifa.hmo_system.on_site_client.gui.staff;

import il.cshaifa.hmo_system.Utils;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.on_site_client.events.StaffNextAppointmentEvent;
import il.cshaifa.hmo_system.on_site_client.events.ViewAppointmentEvent;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;

public class StaffQueueViewController extends ViewController {

  @FXML private Button call_next_patient_button;
  @FXML private TableView<AppointmentPatientRow> appt_table;
  @FXML private TableColumn<AppointmentPatientRow, LocalTime> appt_time;
  @FXML private TableColumn<AppointmentPatientRow, String> appt_type_name;
  @FXML private Label current_date;
  @FXML private TableColumn<AppointmentPatientRow, String> patient_home_clinic;
  @FXML private TableColumn<AppointmentPatientRow, String> patient_name;
  @FXML private TableColumn<AppointmentPatientRow, String> place_in_line;
  @FXML private Label staff_member_role_name;

  private final User staff_member;

  @FXML private ContextMenu queueContextMenu;

  public StaffQueueViewController(User staff_member) {
    this.staff_member = staff_member;
  }

  /**
   * Initializes and sets various view controls
   */
  @FXML
  public void initialize() {
    current_date.setText(Utils.prettifyDateTime(LocalDateTime.now()));
    appt_table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    staff_member_role_name.setText(
        staff_member.getFirstName()
            + " "
            + staff_member.getLastName()
            + ", "
            + staff_member.getRole().getName());
    setCellValueFactory();

    Timeline clock =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                actionEvent -> {
                  var now = LocalDateTime.now();
                  current_date.setText(
                      String.format(
                          "%s:%02d",
                          Utils.prettifyDateTime(now), now.get(ChronoField.SECOND_OF_MINUTE)));
                }),
            new KeyFrame(Duration.seconds(1)));
    clock.setCycleCount(Animation.INDEFINITE);
    clock.play();

    call_next_patient_button.setOnAction(
        actionEvent ->
            EventBus.getDefault()
                .post(StaffNextAppointmentEvent.nextAppointmentRequestEvent(this)));

    queueContextMenu
        .getItems()
        .get(0)
        .setOnAction(
            actionEvent -> {
              var appt = appt_table.getSelectionModel().getSelectedItem();
              if (appt != null) {
                EventBus.getDefault().post(new ViewAppointmentEvent(appt.getQ_appt(), this));
              }
            });
  }

  void setCellValueFactory() {
    place_in_line.setCellValueFactory((new PropertyValueFactory<>("Place_in_line")));
    appt_time.setCellValueFactory((new PropertyValueFactory<>("Appt_time")));
    appt_type_name.setCellValueFactory((new PropertyValueFactory<>("Appt_type_name")));
    patient_home_clinic.setCellValueFactory((new PropertyValueFactory<>("Patient_home_clinic")));
    patient_name.setCellValueFactory((new PropertyValueFactory<>("Patient_name")));
  }

  void populateAppointmentsTable(List<QueuedAppointment> appt_list) {

    while (queueContextMenu.isShowing()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignored) {
      }
    }

    ArrayList<AppointmentPatientRow> appts_to_populate = new ArrayList<AppointmentPatientRow>();

    if (appt_list != null) {
      for (var appt : appt_list) {
        appts_to_populate.add(new AppointmentPatientRow(appt));
      }
    }

    appts_to_populate.sort(Comparator.comparing(AppointmentPatientRow::getPlace_in_line));

    appt_table.getItems().setAll(appts_to_populate);
  }

  public static class AppointmentPatientRow {
    private final String place_in_line;
    private final String appt_time;
    private final String appt_type_name;
    private final String patient_home_clinic;
    private final String patient_name;

    private final QueuedAppointment q_appt;

    public AppointmentPatientRow(QueuedAppointment q_appt) {
      this.q_appt = q_appt;
      this.place_in_line = q_appt.place_in_line;
      this.appt_time = Utils.prettifyDateTime(q_appt.appointment.getDate());
      this.appt_type_name = q_appt.appointment.getType().getName();
      this.patient_home_clinic = q_appt.appointment.getPatient().getHome_clinic().getName();
      this.patient_name = q_appt.appointment.getPatient().getUser().toString();
    }

    public String getPlace_in_line() {
      return place_in_line;
    }

    public String getAppt_time() {
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

    public QueuedAppointment getQ_appt() {
      return q_appt;
    }
  }
}
