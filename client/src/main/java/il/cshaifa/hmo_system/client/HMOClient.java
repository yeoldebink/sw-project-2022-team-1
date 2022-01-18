package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.CommonEnums.SetAppointmentAction;
import il.cshaifa.hmo_system.CommonEnums.StaffAssignmentAction;
import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import il.cshaifa.hmo_system.client.events.ClinicStaffEvent;
import il.cshaifa.hmo_system.client.events.GreenPassStatusEvent;
import il.cshaifa.hmo_system.client.events.LoginEvent;
import il.cshaifa.hmo_system.client.events.LoginEvent.Response;
import il.cshaifa.hmo_system.client.events.ReportEvent;
import il.cshaifa.hmo_system.client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.client.events.WarningEvent;
import il.cshaifa.hmo_system.client.ocsf.AbstractClient;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.entities.Warning;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.RequestType;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.ClinicStaffMessage;
import il.cshaifa.hmo_system.messages.GreenPassStatusMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.MessageType;
import il.cshaifa.hmo_system.messages.ReportMessage;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage;
import il.cshaifa.hmo_system.messages.SetSpecialistAppointmentMessage;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class HMOClient extends AbstractClient {

  private static HMOClient client = null;
  public boolean msg = false;
  private User connected_user;
  private List<Clinic> connected_employee_clinics;
  private Patient connected_patient;

  private HMOClient(String host, int port) {
    super(host, port);
  }

  public User getConnected_user() {
    return connected_user;
  }

  public List<Clinic> getConnected_employee_clinics() {
    return connected_employee_clinics;
  }

  public Patient getConnected_patient() {
    return connected_patient;
  }

  public static HMOClient getClient() {
    if (client == null) {
      client = new HMOClient("localhost", 3000);
    }
    return client;
  }

  /**
   * @param message the message sent. This message can be of several types, handled by controller.
   */
  @Override
  protected void handleMessageFromServer(Object message) {
    this.msg = true;
    if (message.getClass().equals(Warning.class)) {
      EventBus.getDefault().post(new WarningEvent((Warning) message));
    } else {
      if (message.getClass().equals(LoginMessage.class)) {
        this.connected_user = ((LoginMessage) message).user;
        this.connected_patient = ((LoginMessage) message).patient_data;
        handleLoginMessage((LoginMessage) message);
      } else if (message.getClass().equals(ClinicMessage.class)) {
        handleClinicMessage((ClinicMessage) message);
      } else if (message.getClass().equals(ClinicStaffMessage.class)) {
        handleStaffMessage((ClinicStaffMessage) message);
      } else if (message.getClass().equals(AppointmentMessage.class)) {
        handleAppointmentMessage((AppointmentMessage) message);
      } else if (message.getClass().equals(StaffAssignmentMessage.class)) {
        handleStaffAssignmentMessage((StaffAssignmentMessage) message);
      } else if (message.getClass().equals(AdminAppointmentMessage.class)) {
        handleAdminAppointmentMessage((AdminAppointmentMessage) message);
      } else if (message.getClass().equals(ReportMessage.class)) {
        handleReportMessage((ReportMessage) message);
      } else if (message.getClass().equals(SetAppointmentMessage.class)) {
        handleSetAppointmentMessage((SetAppointmentMessage) message);
      } else if (message.getClass().equals(SetSpecialistAppointmentMessage.class)) {
        handleSpecialistAppointmentMessage((SetSpecialistAppointmentMessage) message);
      } else if (message.getClass().equals(GreenPassStatusMessage.class)){
        handleGreenPassStatusMessage((GreenPassStatusMessage) message);
      }
    }
  }

  private void handleGreenPassStatusMessage(GreenPassStatusMessage message) {
    EventBus.getDefault().post(new GreenPassStatusEvent(this, message.last_vaccine, message.last_covid_test,
        message.valid, message.status));
  }

  private void handleSpecialistAppointmentMessage(SetSpecialistAppointmentMessage message) {
    if (message.request == SetSpecialistAppointmentMessage.RequestType.GET_APPOINTMENTS) {
      EventBus.getDefault()
          .post(new AppointmentListEvent((ArrayList<Appointment>) message.appointments, this));
    } else if (message.request == SetSpecialistAppointmentMessage.RequestType.GET_ROLES) {
      EventBus.getDefault().post(new SetAppointmentEvent(this, message.role_list));
    }
  }

  private void handleSetAppointmentMessage(SetAppointmentMessage message) {
    if (message.action == SetAppointmentAction.LOCK) return;

    SetAppointmentEvent event =
        new SetAppointmentEvent(this, getConnected_patient(), message.appointment);
    event.action = message.action;
    if (message.success) {
      event.response = SetAppointmentEvent.ResponseType.AUTHORIZE;
    } else {
      event.response = SetAppointmentEvent.ResponseType.REJECT;
    }
    EventBus.getDefault().post(event);
  }

  private void handleReportMessage(ReportMessage message) {
    ReportEvent event =
        new ReportEvent(
            message.clinics,
            message.report_type,
            message.start_date,
            message.end_date,
            message.reports,
            this);
    EventBus.getDefault().post(event);
  }

  private void handleAdminAppointmentMessage(AdminAppointmentMessage message) {
    AddAppointmentEvent event = new AddAppointmentEvent(this);
    event.success = message.success;
    event.reject = message.reject;
    EventBus.getDefault().post(event);
  }

  private void handleAppointmentMessage(AppointmentMessage message) {
    if (message.request == AppointmentMessage.RequestType.STAFF_FUTURE_APPOINTMENTS) {
      EventBus.getDefault()
          .post(
              new AdminAppointmentListEvent(
                  message.user, (ArrayList<Appointment>) message.appointments, this));
    } else {
      // Applies for:
      // CLINIC_APPOINTMENTS, PATIENT_HISTORY, STAFF_MEMBER_DAILY_APPOINTMENTS, NEXT_APPOINTMENT
      EventBus.getDefault()
          .post(new AppointmentListEvent((ArrayList<Appointment>) message.appointments, this));
    }
  }

  private void handleStaffMessage(ClinicStaffMessage message) {
    ClinicStaffEvent event =
        new ClinicStaffEvent((ArrayList<ClinicStaff>) message.staff_list, this);
    EventBus.getDefault().post(event);
  }

  private void handleStaffAssignmentMessage(StaffAssignmentMessage message) {
    AssignStaffEvent event = new AssignStaffEvent(null, this, null);
    EventBus.getDefault().post(event);
  }

  private void handleClinicMessage(ClinicMessage message) {
    if (message.message_type == MessageType.REQUEST) return;
    ArrayList<Clinic> clinics = (ArrayList<Clinic>) message.clinics;
    ClinicEvent event = new ClinicEvent(clinics, this);
    EventBus.getDefault().post(event);
  }

  private void handleLoginMessage(LoginMessage message) {
    LoginEvent event = new LoginEvent(0, "", this);
    if (message.user == null) {
      event.response = Response.REJECT;
    } else if (message.already_logged_in) {
      event.response = Response.LOGGED_IN;
    } else {
      event.id = message.id;
      event.password = message.password;
      event.response = Response.AUTHORIZE;
      event.userData = message.user;
      event.patientData = message.patient_data;
      this.connected_employee_clinics = message.employee_clinics;
    }
    EventBus.getDefault().post(event);
  }

  /**
   * Request from server a list of all clinics
   *
   * @throws IOException SQL exception
   */
  public void getClinics() throws IOException {
    client.sendToServer(new ClinicMessage());
  }

  /** Requests all HMO staff */
  public void getStaff() throws IOException {
    client.sendToServer(new ClinicStaffMessage());
  }

  /** Requests a staff member's future appointments */
  public void getStaffMemberFutureAppointments(User staff_member_user) throws IOException {
    client.sendToServer(
        new AppointmentMessage(
            staff_member_user, AppointmentMessage.RequestType.STAFF_FUTURE_APPOINTMENTS));
  }

  /** Opens in DB new appointments */
  public void createAppointments(
      User staff_member, LocalDateTime start_time, int count, AppointmentType appt_type)
      throws IOException {
    client.sendToServer(
        new AdminAppointmentMessage(
            RequestType.CREATE,
            staff_member,
            connected_employee_clinics.get(0),
            start_time,
            count,
            null,
            appt_type));
  }

  /** delete given appts from db * */
  public void deleteAppointments(ArrayList<Appointment> appointments_to_delete) throws IOException {
    client.sendToServer(
        new AdminAppointmentMessage(
            RequestType.DELETE, null, null, null, 0, appointments_to_delete, null));
  }

  /** Requests from server all of the connected patients appointments, past & future */
  public void getPatientHistory() throws IOException {
    client.sendToServer(
        new AppointmentMessage(
            this.connected_patient, AppointmentMessage.RequestType.PATIENT_HISTORY));
  }

  /**
   * Requests from server available appointments for next 3 weeks in home clinic of certain type
   *
   * @param type The type of appointment the user requested family doctor/covid test...
   */
  public void getFamilyDoctorAppointments(AppointmentType type) throws IOException {
    // TODO: Change name to getClinicAppointment(type)
    AppointmentMessage appt_msg =
        new AppointmentMessage(
            this.connected_patient, AppointmentMessage.RequestType.CLINIC_APPOINTMENTS);
    appt_msg.type = type;
    appt_msg.clinic = connected_patient.getHome_clinic();
    client.sendToServer(appt_msg);
  }

  /** locks the requested appointment * */
  public void lockAppointment(Appointment appointment) throws IOException {
    client.sendToServer(
        new SetAppointmentMessage(SetAppointmentAction.LOCK, connected_patient, appointment));
  }

  /** takes the requested appointment * */
  public void takeAppointment(Appointment appointment) throws IOException {
    client.sendToServer(
        new SetAppointmentMessage(SetAppointmentAction.TAKE, connected_patient, appointment));
  }

  public void cancelAppointment(Appointment appointment) throws IOException {
    client.sendToServer(
        new SetAppointmentMessage(SetAppointmentAction.RELEASE, connected_patient, appointment));
  }

  /** Requests from server all of today's appointments of current connected staff member client */
  public void getStaffDailyAppointments() throws IOException {
    client.sendToServer(
        new AppointmentMessage(
            this.connected_user, AppointmentMessage.RequestType.STAFF_MEMBER_DAILY_APPOINTMENTS));
  }

  public void getStaffAppointments(User staff_member) throws IOException {
    client.sendToServer(
        new AppointmentMessage(
            staff_member, AppointmentMessage.RequestType.STAFF_FUTURE_APPOINTMENTS));
  }

  public void getSpecialistRoles() throws IOException {
    client.sendToServer(new SetSpecialistAppointmentMessage());
  }

  public void getSpecialistAppointments(Role role) throws IOException {
    client.sendToServer(new SetSpecialistAppointmentMessage(role, connected_patient));
  }

  public void getPatientNextAppointment() throws IOException {
    client.sendToServer(new AppointmentMessage(connected_patient, AppointmentMessage.RequestType.PATIENT_NEXT_APPOINTMENT));
  }

  /**
   * Sends to server a request to get report
   *
   * @param clinics list of clinics we want to get reports about
   * @param start_date the day we want the report to begin
   * @param end_date the day we want the report to end
   * @param report_type attendance / missed / avg. waiting time
   * @throws IOException
   */
  public void requestReports(
      List<Clinic> clinics,
      LocalDateTime start_date,
      LocalDateTime end_date,
      ReportType report_type)
      throws IOException {
    client.sendToServer(new ReportMessage(clinics, report_type, start_date, end_date));
  }

  /**
   * Receives a changed clinic object and updates it in DB
   *
   * @param clinic clinic to be updated at the DB
   * @throws IOException SQL exception
   */
  public void updateClinic(Clinic clinic) throws IOException {
    client.sendToServer(new ClinicMessage(clinic));
    // ensure new clinic data is saved locally as well for HMO manager
    if (connected_user.getRole().getName().equals("Clinic Manager"))
      connected_employee_clinics.set(0, clinic);
  }

  /** Receives a list of staff members to assign to the current Clinic Manager's clinic */
  public void assignStaff(List<User> staff) throws IOException {
    StaffAssignmentMessage message =
        new StaffAssignmentMessage(
            staff, connected_employee_clinics.get(0), StaffAssignmentAction.ASSIGN);
    client.sendToServer(message);
  }

  /** Receives a list of staff members to unassign from the current Clinic Manager's clinic */
  public void unassignStaff(List<User> staff) throws IOException {
    StaffAssignmentMessage message =
        new StaffAssignmentMessage(
            staff, connected_employee_clinics.get(0), StaffAssignmentAction.UNASSIGN);
    client.sendToServer(message);
  }

  public void getGreenPassStatus() throws IOException{
    client.sendToServer(new GreenPassStatusMessage(connected_patient));
  }

  /**
   * @param user The id of the login request
   * @param password The password the user has entered
   * @throws IOException SQL exception
   */
  public void loginRequest(int user, String password) throws IOException {
    client.sendToServer(new LoginMessage(user, password));
  }
}
