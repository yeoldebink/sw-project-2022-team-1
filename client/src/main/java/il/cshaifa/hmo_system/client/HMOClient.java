package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.events.AddAppointmentEvent;
import il.cshaifa.hmo_system.client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AssignStaffEvent;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import il.cshaifa.hmo_system.client.events.ClinicStaffEvent;
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
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.AdminAppointmentMessageType;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.AppointmentMessage.AppointmentRequestType;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.ClinicStaffMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.MessageType;
import il.cshaifa.hmo_system.messages.ReportMessage;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage.Action;
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
      }
    }
  }

  private void handleSetAppointmentMessage(SetAppointmentMessage message) {
    SetAppointmentEvent.Action action;
    if (message.success) {
      action = SetAppointmentEvent.Action.AUTHORIZE;
    } else {
      action = SetAppointmentEvent.Action.REJECT;
    }
    SetAppointmentEvent event =
        new SetAppointmentEvent(this, action, getConnected_patient(), message.appointment);
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
    var event = new AddAppointmentEvent(null, null, null, this);
    event.response_type = message.type;
    event.rejectionType = message.rejectionType;
    EventBus.getDefault().post(event);
  }

  private void handleAppointmentMessage(AppointmentMessage message) {
    Object event = null;

    if (message.requestType == AppointmentRequestType.STAFF_FUTURE_APPOINTMENTS) {
      event =
          new AdminAppointmentListEvent(
              message.user, (ArrayList<Appointment>) message.appointments, this);
    } else if (message.requestType == AppointmentRequestType.PATIENT_HISTORY
        || message.requestType == AppointmentRequestType.CLINIC_APPOINTMENTS) {
      event = new AppointmentListEvent((ArrayList<Appointment>) message.appointments, this);
    }

    EventBus.getDefault().post(event);
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
            staff_member_user, AppointmentRequestType.STAFF_FUTURE_APPOINTMENTS));
  }

  /** Opens in DB new appointments */
  public void createAppointments(
      User staff_member, LocalDateTime start_time, int count, AppointmentType appt_type)
      throws IOException {
    client.sendToServer(
        new AdminAppointmentMessage(
            AdminAppointmentMessageType.CREATE,
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
            AdminAppointmentMessageType.DELETE, null, null, null, 0, appointments_to_delete, null));
  }

  /** Requests from server all of the connected patients appointments, past & future */
  public void getPatientHistory() throws IOException {
    client.sendToServer(
        new AppointmentMessage(this.connected_user, AppointmentRequestType.PATIENT_HISTORY));
  }

  /**
   * Requests from server available appointments for next 3 weeks in home clinic of certain type
   *
   * @param type The type of appointment the user requested family doctor/covid test...
   */
  public void getFamilyDoctorAppointments(AppointmentType type) throws IOException {
    AppointmentMessage appt_msg =
        new AppointmentMessage(this.connected_user, AppointmentRequestType.CLINIC_APPOINTMENTS);
    appt_msg.type = type;
    appt_msg.clinic = connected_patient.getHome_clinic();
    client.sendToServer(appt_msg);
  }

  /** locks the requested appointment * */
  public void lockAppointment(Appointment appointment) throws IOException {
    client.sendToServer(new SetAppointmentMessage(Action.LOCK, connected_patient, appointment));
  }

  /** takes the requested appointment * */
  public void takeAppointment(Appointment appointment) throws IOException {
    client.sendToServer(new SetAppointmentMessage(Action.TAKE, connected_patient, appointment));
  }

  public void cancelAppointment(Appointment appointment) throws IOException {
    client.sendToServer(new SetAppointmentMessage(Action.RELEASE, connected_patient, appointment));
  }

  /** Requests from server all of today's appointments of current connected staff member client */
  public void getStaffDailyAppointments() throws IOException {
    client.sendToServer(
        new AppointmentMessage(
            this.connected_user, AppointmentRequestType.STAFF_MEMBER_DAILY_APPOINTMENTS));
  }

  public void getStaffAppointments(User staff_member) throws IOException {
    client.sendToServer(
        new AppointmentMessage(staff_member, AppointmentRequestType.STAFF_FUTURE_APPOINTMENTS));
  }

  public void getSpecialistRoles() {}

  public void getSpecialistAppointments(Role role) {}

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
  }

  /**
   * Receives a list of staff members to assign or unassign from the current Clinic Manager's clinic
   */
  public void assignOrUnassignStaff(List<User> staff, StaffAssignmentMessage.Type type)
      throws IOException {
    StaffAssignmentMessage message =
        new StaffAssignmentMessage(staff, connected_employee_clinics.get(0), type);
    client.sendToServer(message);
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
