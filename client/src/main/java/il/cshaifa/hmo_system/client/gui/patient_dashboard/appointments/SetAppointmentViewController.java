package il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;

public class SetAppointmentViewController extends ViewController {
  private final Patient patient;

  @FXML private Label clinicNameLabel;

  @FXML private StackPane stackPane;

  @FXML private Accordion chooseApptTypeAccordion;

  @FXML private AnchorPane appointmentsTablePane;
  @FXML private TableView<AppointmentRow> appointmentsTable;
  @FXML private TableColumn<AppointmentRow, String> clinicNameColumn;
  @FXML private TableColumn<AppointmentRow, String> clinicAddressColumn;
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
    apptDateColumn.setCellValueFactory(new PropertyValueFactory<>("AppointmentDateTime"));
  }

  public void populateAppointmentsTable(List<Appointment> appointments) {
    appointmentsTable.getItems().clear();
    for (var appt : appointments) appointmentsTable.getItems().add(new AppointmentRow(appt));
  }

  public void switchToPane(Object pane) {
    for (var p : stackPane.getChildren()) {
      p.setVisible(p.equals(pane));
    }
  }

  @FXML
  public void setAppointmentWithGP(ActionEvent event) {
    switchToPane(appointmentsTablePane);
    SetAppointmentEvent apptEvent = new SetAppointmentEvent(this, null, null, null);
    apptEvent.appointmentType = new AppointmentType("Family Doctor");
    EventBus.getDefault().post(apptEvent);
  }

  @FXML
  public void backToChooseType(ActionEvent event) {
    switchToPane(chooseApptTypeAccordion);
  }

  static class AppointmentRow {
    private final Appointment appointment;

    public AppointmentRow(Appointment appointment) {
      this.appointment = appointment;
    }

    public String getClinicName() {
      return appointment.getClinic().getName();
    }

    public String getClinicAddress() {
      return appointment.getClinic().getAddress();
    }

    public String getAppointmentDateTime() {
      var date = appointment.getDate();

      return String.format("%s, %s %s %s:%s",
          // day of week in 3-letter format
          date.getDayOfWeek().toString().substring(0, 2),
          date.getMonth(),
          date.getDayOfMonth(),
          date.getHour(),
          date.getMinute());
    }
  }
}
