package il.cshaifa.hmo_system.client.gui.patient_dashboard.appointments;

import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent.Action;
import il.cshaifa.hmo_system.client.utils.Utils;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.greenrobot.eventbus.EventBus;
import org.kordamp.ikonli.javafx.FontIcon;

public class SetAppointmentViewController extends ViewController {
  private final Patient patient;
  private HashMap<LocalDate, ArrayList<Appointment>> appointmentsByDate;
  private AppointmentType lastUpdatedAppointmentType;

  @FXML private Label clinicNameLabel;

  @FXML private StackPane stackPane;

  @FXML private Accordion chooseApptTypeAccordion;
  @FXML private VBox gpAppointmentVBox;
  @FXML private Button gpAppointmentsButton;

  private final DatePicker apptDatePicker;
  private Pane apptDatePickerParent;

  @FXML private AnchorPane appointmentsTablePane;
  @FXML private TableView<AppointmentRow> appointmentsTable;
  @FXML private TableColumn<AppointmentRow, String> clinicNameColumn;
  @FXML private TableColumn<AppointmentRow, String> clinicAddressColumn;
  @FXML private TableColumn<AppointmentRow, String> apptDoctorColumn;
  @FXML private TableColumn<AppointmentRow, String> apptDateColumn;

  @FXML private Button setAppointmentButton;

  @FXML private Label errorLabel;

  public SetAppointmentViewController(Patient patient) {
    this.patient = patient;
    apptDatePicker = new DatePicker();
    apptDatePicker.setPromptText("Select a date");
  }

  @FXML
  public void initialize() {
    errorLabel.setTextFill(Color.DARKRED);
    setAppointmentButton.setDisable(true);

    chooseApptTypeAccordion.setExpandedPane(chooseApptTypeAccordion.getPanes().get(0));
    gpAppointmentsButton.setOnAction(
        (event) -> {
          errorLabel.setVisible(false);
          requestAppointments(new AppointmentType("Family Doctor"));
        });

    clinicNameLabel.setText(patient.getHome_clinic().getName());

    switchToPane(chooseApptTypeAccordion);

    // set cell value factories
    clinicNameColumn.setCellValueFactory(new PropertyValueFactory<>("ClinicName"));
    clinicAddressColumn.setCellValueFactory(new PropertyValueFactory<>("ClinicAddress"));
    apptDoctorColumn.setCellValueFactory(new PropertyValueFactory<>("AppointmentDoctor"));
    apptDateColumn.setCellValueFactory(new PropertyValueFactory<>("AppointmentDateTime"));

    apptDatePicker
        .valueProperty()
        .addListener(
            (ov, oldValue, newValue) -> {
              if (newValue != null) {
                populateAppointmentsTable(newValue);
                apptDatePicker.setValue(null);
              }
            });

    appointmentsTable
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldSelection, newSelection) -> {
              setAppointmentButton.setDisable(newSelection == null);
              if (newSelection == null) {
                EventBus.getDefault()
                    .post(
                        new SetAppointmentEvent(
                            this, Action.RELEASE, patient, oldSelection.getAppointment()));
              } else {
                EventBus.getDefault()
                    .post(
                        new SetAppointmentEvent(
                            this, Action.LOCK, patient, newSelection.getAppointment()));
              }
            });
  }

  private void moveApptDatePicker(AppointmentType apptType) {
    // now we need to put the datepicker where it belongs
    if (apptDatePickerParent != null) apptDatePickerParent.getChildren().remove(apptDatePicker);

    Pane newParent;

    switch (apptType.getName()) {
      case "Family Doctor":
      case "Pediatrician":
        newParent = gpAppointmentVBox;
        break;
      default:
        newParent = gpAppointmentVBox;
        new NotImplementedException("Haven't put this shit in yet, fam").printStackTrace();
    }

    newParent.getChildren().add(apptDatePicker);
    apptDatePickerParent = newParent;
  }

  public void populateAppointmentDates(List<Appointment> appointments) {
    if (appointments.size() == 0) {
      errorLabel.setVisible(true);
      return;
    } else {
      errorLabel.setVisible(false);
    }

    // hash appts by date for the picker
    appointmentsByDate = new HashMap<>();

    for (var appt : appointments) {
      var date = appt.getDate().toLocalDate();
      appointmentsByDate.putIfAbsent(date, new ArrayList<>());
      appointmentsByDate.get(date).add(appt);
    }

    apptDatePicker.setDayCellFactory(
        datePicker ->
            new DateCell() {
              public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(!appointmentsByDate.containsKey(date));
              }
            });

    moveApptDatePicker(appointments.get(0).getType());
  }

  private void populateAppointmentsTable(LocalDate date) {
    appointmentsTable.getItems().clear();
    for (var appt : appointmentsByDate.get(date)) {
      appointmentsTable.getItems().add(new AppointmentRow(appt));
    }

    switchToPane(appointmentsTablePane);
  }

  @FXML
  public void takeAppointment(ActionEvent event) {
    EventBus.getDefault()
        .post(
            new SetAppointmentEvent(
                this,
                Action.TAKE,
                patient,
                appointmentsTable.getSelectionModel().getSelectedItem().getAppointment()));
  }

  public void switchToPane(Object pane) {
    for (var p : stackPane.getChildren()) {
      p.setVisible(p.equals(pane));
    }
  }

  public void requestAppointments(AppointmentType apptType) {
    lastUpdatedAppointmentType = apptType;
    SetAppointmentEvent apptEvent = new SetAppointmentEvent(this, null, null, null);
    apptEvent.appointmentType = apptType;
    EventBus.getDefault().post(apptEvent);
  }

  @FXML
  public void backToChooseType(ActionEvent event) {
    requestAppointments(lastUpdatedAppointmentType);
    switchToPane(chooseApptTypeAccordion);
  }

  public void takeAppointmentFailed(boolean success, int dialogX, int dialogY) {
    Stage stage = new Stage();
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10, 10, 10, 10));
    vbox.setSpacing(10);
    vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

    if (!success) {
      Label error = new Label("There was an error processing your request.\nPlease select a different appointment time.");
      FontIcon alert = new FontIcon();
      alert.setIconLiteral("mdi-alert-outline");
      alert.setIconSize(40);
      alert.setIconColor(Color.DARKRED);
      error.setGraphic(alert);
      vbox.getChildren().add(error);
    } else {
      Image image = new Image(getClass().getResourceAsStream("see_you_soon.jpg"));
      ImageView imageView = new ImageView(image);
      vbox.getChildren().add(imageView);
    }

    HBox hbox = new HBox();
    hbox.setAlignment(Pos.CENTER_RIGHT);

    Button okButton = new Button("Back");
    FontIcon back = new FontIcon();
    back.setIconLiteral("mdi-undo-variant");
    back.setIconSize(20);
    okButton.setGraphic(back);

    okButton.setOnAction((event) -> stage.close());

    hbox.getChildren().add(okButton);
    vbox.getChildren().add(hbox);

    stage.setScene(new Scene(vbox));
    stage.initModality(Modality.APPLICATION_MODAL);

    stage.setX(dialogX);
    stage.setY(dialogY);

    stage.showAndWait();

    backToChooseType(null);
  }

  public static class AppointmentRow {
    private final Appointment appointment;

    public AppointmentRow(Appointment appointment) {
      this.appointment = appointment;
    }

    public Appointment getAppointment() {
      return appointment;
    }

    public String getClinicName() {
      return appointment.getClinic().getName();
    }

    public String getClinicAddress() {
      return appointment.getClinic().getAddress();
    }

    public String getAppointmentDoctor() {
      return "Dr. "
          + appointment.getStaff_member().getFirstName()
          + " "
          + appointment.getStaff_member().getLastName();
    }

    public String getAppointmentDateTime() {
      return Utils.prettifyDateTime(appointment.getDate());
    }
  }
}
