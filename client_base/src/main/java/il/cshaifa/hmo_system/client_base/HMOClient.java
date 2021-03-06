package il.cshaifa.hmo_system.client_base;

import il.cshaifa.hmo_system.Constants;
import il.cshaifa.hmo_system.client_base.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client_base.events.ClinicEvent;
import il.cshaifa.hmo_system.client_base.events.LoginEvent;
import il.cshaifa.hmo_system.client_base.events.LoginEvent.Response;
import il.cshaifa.hmo_system.client_base.events.WarningEvent;
import il.cshaifa.hmo_system.client_base.ocsf.AbstractClient;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.entities.Warning;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.DesktopLoginMessage;
import il.cshaifa.hmo_system.messages.InitConstantsMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.MessageType;
import il.cshaifa.hmo_system.messages.OnSiteLoginMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
      if (message instanceof LoginMessage) {
        this.connected_user = ((LoginMessage) message).user;
        handleLoginMessage((LoginMessage) message, null);
      } else if (message.getClass().equals(ClinicMessage.class)) {
        handleClinicMessage((ClinicMessage) message);
      } else if (message.getClass().equals(AppointmentMessage.class)) {
        handleAppointmentMessage((AppointmentMessage) message);
      } else if (message.getClass().equals(InitConstantsMessage.class)) {
        Constants.init(
            ((InitConstantsMessage) message).appointment_types,
            ((InitConstantsMessage) message).roles);
      }
    }
  }

  protected void handleAppointmentMessage(AppointmentMessage message) {
    // Applies for:
    // CLINIC_APPOINTMENTS, PATIENT_HISTORY, STAFF_MEMBER_DAILY_APPOINTMENTS
    EventBus.getDefault()
        .post(new AppointmentListEvent((ArrayList<Appointment>) message.appointments, this));
  }

  private void handleClinicMessage(ClinicMessage message) {
    if (message.message_type == MessageType.REQUEST) {
      return;
    }
    ArrayList<Clinic> clinics = (ArrayList<Clinic>) message.clinics;
    ClinicEvent event = new ClinicEvent(clinics, this);
    EventBus.getDefault().post(event);
  }

  protected void handleLoginMessage(LoginMessage message, LoginEvent _event) {

    // this allows the on-site client to provide a pre-made LoginEvent for posting
    // such an event may contain staff member queue information
    LoginEvent event = _event == null ? new LoginEvent(0, "", this) : _event;
    if (message.user == null) {
      event.response = Response.REJECT;
    } else if (message instanceof DesktopLoginMessage
        && ((DesktopLoginMessage) message).already_logged_in) {
      event.response = Response.LOGGED_IN;
    } else {
      event.id = message.id;
      event.password = message.password;
      event.response = Response.AUTHORIZE;
      event.userData = message.user;
      if (message instanceof DesktopLoginMessage) {
        event.patientData = ((DesktopLoginMessage) message).patient_data;
        this.connected_employee_clinics = ((DesktopLoginMessage) message).employee_clinics;
      } else {
        this.connected_employee_clinics =
            Collections.singletonList(((OnSiteLoginMessage) message).clinic);
      }
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
}
