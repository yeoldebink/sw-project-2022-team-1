package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.events.AdminAppointmentListEvent;
import il.cshaifa.hmo_system.client.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import il.cshaifa.hmo_system.client.events.LoginEvent;
import il.cshaifa.hmo_system.client.events.LoginEvent.Response;
import il.cshaifa.hmo_system.client.events.NextAppointmentEvent;
import il.cshaifa.hmo_system.client.events.WarningEvent;
import il.cshaifa.hmo_system.client.ocsf.AbstractClient;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.entities.Warning;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.MessageType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public abstract class HMOClient extends AbstractClient {

  protected User connected_user;
  protected List<Clinic> connected_employee_clinics;

  protected HMOClient(String host, int port) {
    super(host, port);
  }

  public User getConnected_user() {
    return connected_user;
  }

  public List<Clinic> getConnected_employee_clinics() {
    return connected_employee_clinics;
  }

  /**
   * @param message the message sent. This message can be of several types, handled by controller.
   */
  @Override
  protected void handleMessageFromServer(Object message) {
    if (message.getClass().equals(Warning.class)) {
      EventBus.getDefault().post(new WarningEvent((Warning) message));
    } else {
      if (message.getClass().equals(LoginMessage.class)) {
        this.connected_user = ((LoginMessage) message).user;
        handleLoginMessage((LoginMessage) message);
      } else if (message.getClass().equals(ClinicMessage.class)) {
        handleClinicMessage((ClinicMessage) message);
      } else if (message.getClass().equals(AppointmentMessage.class)) {
        handleAppointmentMessage((AppointmentMessage) message);
      }
    }
  }

  private void handleAppointmentMessage(AppointmentMessage message) {
    if (message.request == AppointmentMessage.RequestType.STAFF_FUTURE_APPOINTMENTS) {
      EventBus.getDefault()
          .post(
              new AdminAppointmentListEvent(
                  message.user, (ArrayList<Appointment>) message.appointments, this));
    } else if (message.request == AppointmentMessage.RequestType.PATIENT_NEXT_APPOINTMENT) {
      var appt = message.appointments == null ? null : message.appointments.get(0);
      EventBus.getDefault().post(new NextAppointmentEvent(this, appt));
    } else {
      // Applies for:
      // CLINIC_APPOINTMENTS, PATIENT_HISTORY, STAFF_MEMBER_DAILY_APPOINTMENTS
      EventBus.getDefault()
          .post(new AppointmentListEvent((ArrayList<Appointment>) message.appointments, this));
    }
  }

  private void handleClinicMessage(ClinicMessage message) {
    if (message.message_type == MessageType.REQUEST) {
      return;
    }
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
    sendToServer(new ClinicMessage());
  }

  /** Requests from server all of today's appointments of current connected staff member client */
  public void getStaffDailyAppointments() throws IOException {
    sendToServer(
        new AppointmentMessage(
            this.connected_user, AppointmentMessage.RequestType.STAFF_MEMBER_DAILY_APPOINTMENTS));
  }

  /**
   * @param user The id of the login request
   * @param password The password the user has entered
   * @throws IOException SQL exception
   */
  public void loginRequest(int user, String password) throws IOException {
    sendToServer(new LoginMessage(user, password));
  }
}
