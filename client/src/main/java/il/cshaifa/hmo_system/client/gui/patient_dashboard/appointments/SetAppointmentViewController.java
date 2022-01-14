package il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.client.utils.Utils;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;

public class SetAppointmentViewController extends ViewController {
  private final Patient patient;
  private HashMap<LocalDate, ArrayList<Appointment>> appointmentsByDate;

  @FXML private Label clinicNameLabel;

  @FXML private StackPane stackPane;

  @FXML private Accordion chooseApptTypeAccordion;

  @FXML private AnchorPane chooseApptDatePane;
  @FXML private DatePicker apptDatePicker;

  @FXML private AnchorPane appointmentsTablePane;
  @FXML private TableView<AppointmentRow> appointmentsTable;
  @FXML private TableColumn<AppointmentRow, String> clinicNameColumn;
  @FXML private TableColumn<AppointmentRow, String> clinicAddressColumn;
  @FXML private TableColumn<AppointmentRow, String> apptDoctorColumn;
  @FXML private TableColumn<AppointmentRow, String> apptDateColumn;

  public SetAppointmentViewController(Patient patient) {
    this.patient = patient;
  }

  @FXML
  public void initialize() {
    chooseApptTypeAccordion.setExpandedPane(chooseApptTypeAccordion.getPanes().get(0));

    clinicNameLabel.setText(patient.getHome_clinic().getName());

    switchToPane(chooseApptTypeAccordion);

    // set cell value factories
    clinicNameColumn.setCellValueFactory(new PropertyValueFactory<>("ClinicName"));
    clinicAddressColumn.setCellValueFactory(new PropertyValueFactory<>("ClinicAddress"));
    apptDoctorColumn.setCellValueFactory(new PropertyValueFactory<>("AppointmentDoctor"));
    apptDateColumn.setCellValueFactory(new PropertyValueFactory<>("AppointmentDateTime"));

    apptDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
      populateAppointmentsTable();
    });
  }

  public void populateAppointmentDates(List<Appointment> appointments) {
    appointmentsByDate = new HashMap<>();

    for (var appt : appointments) {
      var date = appt.getDate().toLocalDate();
      appointmentsByDate.putIfAbsent(date, new ArrayList<>());
      appointmentsByDate.get(date).add(appt);
    }

    apptDatePicker.setDayCellFactory(datePicker -> new DateCell() {
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(!appointmentsByDate.containsKey(date));
      }
    });

    switchToPane(chooseApptDatePane);
  }

  private void populateAppointmentsTable() {
    appointmentsTable.getItems().clear();
    for (var appt : appointmentsByDate.get(apptDatePicker.getValue())) {
      appointmentsTable.getItems().add(new AppointmentRow(appt));
    }

    switchToPane(appointmentsTablePane);
  }

  public void switchToPane(Object pane) {
    for (var p : stackPane.getChildren()) {
      p.setVisible(p.equals(pane));
    }
  }

  @FXML
  public void setAppointmentWithGP(ActionEvent event) {
    SetAppointmentEvent apptEvent = new SetAppointmentEvent(this, null, null, null);
    apptEvent.appointmentType = new AppointmentType("Family Doctor");
    EventBus.getDefault().post(apptEvent);
  }

  @FXML
  public void backToChooseType(ActionEvent event) {
    switchToPane(chooseApptTypeAccordion);
  }

  public static class AppointmentRow {
    private final Appointment appointment;

    public AppointmentRow(Appointment appointment) {
      this.appointment = appointment;
    }

    public Appointment getAppointment() {return appointment;}

    public String getClinicName() {
      return appointment.getClinic().getName();
    }

    public String getClinicAddress() {
      return appointment.getClinic().getAddress();
    }

    public String getAppointmentDoctor() {
      return "Dr. " + appointment.getStaff_member().getFirstName() + " " + appointment.getStaff_member().getLastName();
    }

    public String getAppointmentDateTime() {
      return Utils.prettifyDateTime(appointment.getDate());
    }
  }
}
