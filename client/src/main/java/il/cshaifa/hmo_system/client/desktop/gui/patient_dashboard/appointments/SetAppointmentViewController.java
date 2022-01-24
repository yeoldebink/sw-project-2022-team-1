package il.cshaifa.hmo_system.client.desktop.gui.patient_dashboard.appointments;

import il.cshaifa.hmo_system.CommonEnums.SetAppointmentAction;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.desktop.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.client.utils.Utils;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.ComboBoxListCell;
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
import javafx.util.Callback;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.greenrobot.eventbus.EventBus;
import org.kordamp.ikonli.javafx.FontIcon;

public class SetAppointmentViewController extends ViewController {
  private final Patient patient;
  private HashMap<LocalDate, ArrayList<Appointment>> appointmentsByDate;
  private HashMap<User, ArrayList<Appointment>> appointmentsByDoctor;
  private AppointmentType lastUpdatedAppointmentType;
  private Role lastUpdatedSpecialistRole;

  @FXML private Label clinicNameLabel;

  @FXML private StackPane stackPane;

  @FXML private Accordion chooseApptTypeAccordion;

  @FXML private VBox gpAppointmentVBox;
  @FXML private Button gpAppointmentsButton;

  @FXML private VBox spAppointmentsVBox;
  @FXML private ComboBox<Role> spTypeComboBox;
  @FXML private ComboBox<SPDoctorItem> spDoctorComboBox;

  @FXML private VBox testAppointmentsVBox;
  @FXML private ComboBox<String> symptomsComboBox;
  @FXML private TextArea symptomsTextArea;
  @FXML private Button testAppointmentsButton;

  @FXML private VBox vaxAppointmentsVBox;
  @FXML private ComboBox<AppointmentType> vaxTypeComboBox;

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
              if (newSelection != null) {
                EventBus.getDefault()
                    .post(
                        new SetAppointmentEvent(
                            this,
                            SetAppointmentAction.LOCK,
                            patient,
                            newSelection.getAppointment()));
              }
            });

    // GP GUI

    gpAppointmentsButton.setOnAction(
        (event) -> {
          errorLabel.setVisible(false);
          requestAppointments(new AppointmentType("Family Doctor"), null);
        });

    // SPECIALIST GUI

    Callback<ListView<Role>, ListCell<Role>> spComboCellFactory =
        new Callback<>() {
          @Override
          public ListCell<Role> call(ListView<Role> roleList) {
            return new ComboBoxListCell<>() {
              @Override
              public void updateItem(Role role, boolean empty) {
                super.updateItem(role, empty);
                if (!empty) {
                  setText(role.getName());
                  String iconLiteral = null;
                  switch (role.getName()) {
                    case "Cardiologist":
                      iconLiteral = "mdi-heart-pulse";
                      break;
                    case "Neurologist":
                      iconLiteral = "mdi-lightbulb-outline";
                      break;
                    case "Endocrinologist":
                      iconLiteral = "mdi-invert-colors";
                      break;
                    case "Dermatologist":
                      iconLiteral = "mdi-fingerprint";
                      break;
                    case "Orthopedist":
                      iconLiteral = "mdi-wrench";
                      break;
                    default:
                      new NotImplementedException(
                              String.format("Specialist role not implemented: %s", role.getName()))
                          .printStackTrace();
                  }

                  var icon = new FontIcon();
                  icon.setIconLiteral(iconLiteral);
                  setGraphic(icon);
                }
              }
            };
          }
        };

    spTypeComboBox.setButtonCell(spComboCellFactory.call(null));
    spTypeComboBox.setCellFactory(spComboCellFactory);

    spTypeComboBox
        .valueProperty()
        .addListener(
            (newRole) -> {
              if (spTypeComboBox.getValue() != null) {
                errorLabel.setVisible(false);
                requestAppointments(new AppointmentType("Specialist"), spTypeComboBox.getValue());
              }
            });

    spDoctorComboBox
        .valueProperty()
        .addListener(
            (newDoctor) -> {
              if (spDoctorComboBox.getValue() != null) {
                populateAppointmentDates(
                    appointmentsByDoctor.get(spDoctorComboBox.getValue().getDoctor()), true);
              }
            });

    spDoctorComboBox.setButtonCell(
        new ListCell<>() {
          @Override
          protected void updateItem(SPDoctorItem doctorItem, boolean empty) {
            super.updateItem(doctorItem, empty);
            if (empty || doctorItem == null) {
              setText("Select a clinic and doctor");
            } else {
              setText(doctorItem.toString());
            }
          }
        });

    // VAX GUI

    Callback<ListView<AppointmentType>, ListCell<AppointmentType>> vaxTypeComboCellFactory =
        new Callback<>() {
          @Override
          public ListCell<AppointmentType> call(ListView<AppointmentType> apptTypeList) {
            return new ComboBoxListCell<>() {
              @Override
              public void updateItem(AppointmentType apptType, boolean empty) {
                super.updateItem(apptType, empty);
                if (!empty) {
                  setText(apptType.getName());
                  String iconLiteral = null;
                  switch (apptType.getName()) {
                    case "COVID Vaccine":
                      iconLiteral = "mdi-basecamp";
                      break;
                    case "Flu Vaccine":
                      iconLiteral = "mdi-chemical-weapon";
                      break;
                  }

                  var icon = new FontIcon();
                  icon.setIconLiteral(iconLiteral);
                  setGraphic(icon);
                }
              }
            };
          }
        };

    vaxTypeComboBox.setButtonCell(vaxTypeComboCellFactory.call(null));
    vaxTypeComboBox.setCellFactory(vaxTypeComboCellFactory);

    vaxTypeComboBox
        .getItems()
        .setAll(
            FXCollections.observableArrayList(
                new AppointmentType("COVID Vaccine"), new AppointmentType("Flu Vaccine")));

    vaxTypeComboBox
        .valueProperty()
        .addListener(
            (observableValue, oldT, newT) -> {
              if (newT != null && newT != oldT) {
                requestAppointments(newT, null);
              }
            });

    // COVID TEST GUI
    symptomsComboBox.setCellFactory(
        stringListView ->
            new ComboBoxListCell<>() {
              @Override
              public void updateItem(String str, boolean empty) {
                super.updateItem(str, empty);
                if (!empty) {
                  setText(str);
                  String iconLiteral = null;
                  switch (str) {
                    case "Yes":
                      iconLiteral = "mdi-emoticon-sad";
                      break;
                    case "No":
                      iconLiteral = "mdi-emoticon-happy";
                      break;
                  }

                  var icon = new FontIcon();
                  icon.setIconLiteral(iconLiteral);
                  setGraphic(icon);
                }
              }
            });
    symptomsComboBox.setButtonCell(symptomsComboBox.getCellFactory().call(null));

    symptomsComboBox.getItems().setAll(FXCollections.observableArrayList("No", "Yes"));

    symptomsComboBox
        .valueProperty()
        .addListener(
            (obs, oldStr, newStr) -> {
              testAppointmentsButton.setVisible(true);
              if (newStr.equals("Yes")) {
                symptomsTextArea.setVisible(true);
                if (!newStr.equals(oldStr)) {
                  symptomsTextArea.clear();
                  testAppointmentsButton.setDisable(true);
                }
              } else {
                testAppointmentsButton.setDisable(false);
                symptomsTextArea.setVisible(false);
              }
            });

    symptomsTextArea
        .textProperty()
        .addListener(
            (obs, oldStr, newStr) ->
                testAppointmentsButton.setDisable(newStr == null || newStr.equals("")));

    testAppointmentsButton.setOnAction(
        (event) -> requestAppointments(new AppointmentType("COVID Test"), null));
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
      case "Specialist":
        newParent = spAppointmentsVBox;
        break;
      case "COVID Vaccine":
      case "Flu Vaccine":
        newParent = vaxAppointmentsVBox;
        break;
      case "COVID Test":
        newParent = testAppointmentsVBox;
        break;
      default:
        return;
    }

    newParent.getChildren().add(apptDatePicker);
    apptDatePickerParent = newParent;
  }

  public void populateSpecialistRoles(List<Role> specialistRoles) {
    spTypeComboBox.getItems().setAll(specialistRoles);
  }

  public void populateSpecialistData(List<Appointment> appointments) {
    appointmentsByDoctor = new HashMap<>();

    spDoctorComboBox.getItems().clear();
    HashMap<User, HashSet<Clinic>> doctorClinics = new HashMap<>();

    for (var appt : appointments) {
      var clinic = appt.getClinic();
      var doctor = appt.getStaff_member();
      doctorClinics.putIfAbsent(doctor, new HashSet<>());
      // returns true if the clinic was not already present for this doctor
      if (doctorClinics.get(doctor).add(clinic)) {
        spDoctorComboBox.getItems().add(new SPDoctorItem(doctor, clinic));
      }
      appointmentsByDoctor.putIfAbsent(doctor, new ArrayList<>());
      appointmentsByDoctor.get(doctor).add(appt);
    }

    spDoctorComboBox.setVisible(true);
  }

  public void populateAppointments(List<Appointment> appointments) {
    if (appointments == null || appointments.size() == 0) {
      errorLabel.setVisible(true);
      spDoctorComboBox.setVisible(false);
      apptDatePicker.setVisible(false);
      return;
    } else {
      apptDatePicker.setVisible(true);
      errorLabel.setVisible(false);
    }

    // if specialist, populate the doctor/clinic list
    if (appointments.get(0).getType().getName().equals("Specialist")) {
      populateSpecialistData(appointments);
    } else populateAppointmentDates(appointments, true);
  }

  public void populateAppointmentDates(List<Appointment> appointments, boolean showDates) {
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

    if (showDates) moveApptDatePicker(appointments.get(0).getType());
  }

  private void populateAppointmentsTable(LocalDate date) {
    populateAppointmentsTable(appointmentsByDate.get(date));
  }

  // overloaded for direct population in the case of specialists
  private void populateAppointmentsTable(List<Appointment> appointments) {
    appointmentsTable.getItems().clear();
    for (var appt : appointments) {
      appointmentsTable.getItems().add(new AppointmentRow(appt));
    }
    switchToPane(appointmentsTablePane);
  }

  @FXML
  public void takeAppointment(ActionEvent event) {
    var appt = appointmentsTable.getSelectionModel().getSelectedItem().getAppointment();
    if (lastUpdatedAppointmentType.getName().equals("COVID Test"))
      appt.setComments(
          String.format(
              "Has symptoms: %s\n%s", symptomsComboBox.getValue(), symptomsTextArea.getText()));

    EventBus.getDefault()
        .post(new SetAppointmentEvent(this, SetAppointmentAction.TAKE, patient, appt));
  }

  public void switchToPane(Object pane) {
    for (var p : stackPane.getChildren()) {
      p.setVisible(p.equals(pane));
    }
  }

  public void requestAppointments(AppointmentType apptType, Role role) {
    lastUpdatedAppointmentType = apptType;
    lastUpdatedSpecialistRole = role;
    SetAppointmentEvent apptEvent = new SetAppointmentEvent(this, null, null, null);
    apptEvent.appointmentType = apptType;
    apptEvent.role = role;
    EventBus.getDefault().post(apptEvent);
  }

  @FXML
  public void backToChooseType(ActionEvent event) {
    requestAppointments(lastUpdatedAppointmentType, lastUpdatedSpecialistRole);

    switchToPane(chooseApptTypeAccordion);
  }

  public void takeAppointment(boolean success, int dialogX, int dialogY) {
    Stage stage = new Stage();
    VBox vbox = new VBox();
    vbox.setPadding(new Insets(10, 10, 10, 10));
    vbox.setSpacing(10);
    vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

    if (!success) {
      Label error =
          new Label(
              "There was an error processing your request.\n"
                  + "Please select a different appointment time.");
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
      if (appointment.getStaff_member() != null)
        return "Dr. "
            + appointment.getStaff_member().getFirstName()
            + " "
            + appointment.getStaff_member().getLastName();
      else return "";
    }

    public String getAppointmentDateTime() {
      return Utils.prettifyDateTime(appointment.getDate());
    }
  }

  public static class SPDoctorItem {
    private final User doctor;
    private final Clinic clinic;

    public SPDoctorItem(User doctor, Clinic clinic) {
      this.doctor = doctor;
      this.clinic = clinic;
    }

    public String toString() {
      return String.format(
          "%s • %s %s", clinic.getName(), doctor.getFirstName(), doctor.getLastName());
    }

    public User getDoctor() {
      return doctor;
    }
  }
}
