package il.cshaifa.hmo_system.server.server_handlers;

import static il.cshaifa.hmo_system.Constants.CLINIC_COL;
import static il.cshaifa.hmo_system.Constants.CLINIC_MANAGER;
import static il.cshaifa.hmo_system.Constants.HMO_MANAGER;
import static il.cshaifa.hmo_system.Constants.MANAGER_USER_COL;
import static il.cshaifa.hmo_system.Constants.PATIENT;
import static il.cshaifa.hmo_system.Constants.ROLE;
import static il.cshaifa.hmo_system.Constants.USER_COL;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.Utils;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.DesktopLoginMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.OnSiteLoginMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.server.server_handlers.queues.ClinicQueues;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleLoginMessage extends MessageHandler {
  public LoginMessage class_message;

  private static final ReentrantLock connection_maps_lock;
  private static final HashMap<User, ConnectionToClient> connected_desktop_users;
  private static final HashMap<ConnectionToClient, Clinic> onsite_connections;
  private static final HashMap<Clinic, HashSet<ConnectionToClient>> onsite_connections_by_clinic;

  static {
    connection_maps_lock = new ReentrantLock(true);
    connected_desktop_users = new HashMap<>();
    onsite_connections = new HashMap<>();
    onsite_connections_by_clinic = new HashMap<>();
  }

  public HandleLoginMessage(LoginMessage message, Session session, ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (LoginMessage) this.message;
  }

  /** @return Returns Clinic associated with client */
  public static Clinic stationClinic(ConnectionToClient client) {
    return onsite_connections.get(client);
  }

  private void setUser(User user) {
    this.class_message.user = user;
    this.client.setInfo("user_str", String.format("%s [id: %s]", user.toString(), user.getId()));
    this.client.setInfo("user", user);

    logSuccess(
        String.format(
            "User logged in: %s, %s {%s}",
            client.getInfo("user_str"),
            user.getRole().getName(),
            this.class_message instanceof DesktopLoginMessage ? "Desktop" : "On-site"));
  }

  /** If login successful will update the LoginMessage with user and his details */
  @Override
  public void handleMessage() {
    User user = session.get(User.class, class_message.id);

    boolean is_desktop = this.class_message instanceof DesktopLoginMessage;

    if (user == null) return;

    String user_encoded_password = user.getPassword();
    String entered_password = null;
    try {
      entered_password = Utils.encodePassword(class_message.password, user.getSalt());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    if (!user_encoded_password.equals(entered_password)) return;

    // patients and HMO Mgr are only able to login on the desktop client
    switch (user.getRole().getName()) {
      case PATIENT:
        if (is_desktop) {
          setUser(user);
          ((DesktopLoginMessage) this.class_message).patient_data = getUserPatient(user);
        }
        break;

      case HMO_MANAGER:
        if (is_desktop) {
          setUser(user);
        }
        break;

      default: // this is for Clinic Mgr & clinic staff
        var employee_clinics = employeeClinics(user);
        boolean is_clinic_manager = user.getRole().equals(ROLE(CLINIC_MANAGER));

        var desktop_message =
            class_message instanceof DesktopLoginMessage
                ? (DesktopLoginMessage) class_message
                : null;
        var on_site_message =
            class_message instanceof OnSiteLoginMessage ? (OnSiteLoginMessage) class_message : null;

        boolean works_here =
            on_site_message != null && employee_clinics.contains(on_site_message.clinic);

        if (is_clinic_manager) {
          if (desktop_message != null) {
            desktop_message.employee_clinics = employee_clinics;
            this.setUser(user);

          } else if (on_site_message != null && works_here) {
            this.setUser(user);

            switch (on_site_message.action) {
              case LOGIN:
                connectOnSiteStation();
                break;

              case CLOSE_STATION:
                disconnectOnSiteStation(client);
                break;

              case CLOSE_CLINIC:
                closeClinic();
                break;
            }
          }

          // end desktop case
          // now for employees
        } else if (on_site_message != null
            && works_here
            && on_site_message.action == OnSiteLoginAction.LOGIN) {
          setUser(user);
          connectOnSiteStation();
          var q_update =
              ClinicQueues.connectToQueue(
                  user, ((OnSiteLoginMessage) this.class_message).clinic, client);
          ((OnSiteLoginMessage) class_message).staff_member_queue = q_update.updated_queue;
          ((OnSiteLoginMessage) class_message).queue_timestamp = q_update.timestamp;
        }

        break;
    }

    if (this.class_message.user == null) {
      logFailure(String.valueOf(class_message.id));
    } else {
      logSuccess(String.valueOf(class_message.id));
    }

    if (!is_desktop || this.class_message.user == null) return;

    connection_maps_lock.lock();

    try {
      if (connected_desktop_users.containsKey(user)) {
        ((DesktopLoginMessage) this.class_message).already_logged_in = true;
        logFailure("ALREADY_LOGGED_IN");
      } else {
        connected_desktop_users.put(user, client);
      }
    } finally {
      connection_maps_lock.unlock();
    }
  }

  private List<Clinic> employeeClinics(User user) {
    CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);
    if (user.getRole().equals(ROLE(CLINIC_MANAGER))) {
      Root<Clinic> root = cr.from(Clinic.class);
      cr.select(root).where(cb.equal(root.get(MANAGER_USER_COL), user));
    } else {
      Root<ClinicStaff> root = cr.from(ClinicStaff.class);
      cr.select(root.get(CLINIC_COL)).where(cb.equal(root.get(USER_COL), user));
    }
    return session.createQuery(cr).getResultList();
  }

  private Patient getUserPatient(User user) {
    CriteriaQuery<Patient> cr = cb.createQuery(Patient.class);
    Root<Patient> root = cr.from(Patient.class);
    cr.select(root).where(cb.equal(root.get(USER_COL), user));
    return session.createQuery(cr).getResultList().get(0);
  }

  /** Adds (clinic, client) information to relevant data structures */
  public void connectOnSiteStation() {
    connection_maps_lock.lock();
    try {
      var clinic = ((OnSiteLoginMessage) class_message).clinic;
      onsite_connections.put(this.client, clinic);
      onsite_connections_by_clinic.putIfAbsent(clinic, new HashSet<>());
      onsite_connections_by_clinic.get(clinic).add(client);
    } finally {
      connection_maps_lock.unlock();
    }
  }

  /** Removes client information from relevant data structures */
  public static void disconnectOnSiteStation(ConnectionToClient client) {
    connection_maps_lock.lock();
    try {
      var clinic = onsite_connections.remove(client);
      onsite_connections_by_clinic.get(clinic).remove(client);

      if (onsite_connections_by_clinic.get(clinic).size() == 0) {
        onsite_connections_by_clinic.remove(clinic);
        ClinicQueues.closeClinic(clinic);
      }
    } finally {
      connection_maps_lock.unlock();
    }
  }

  /** Removes clinic information from relevant data structures */
  public void closeClinic() {
    connection_maps_lock.lock();
    try {
      var clinic = ((OnSiteLoginMessage) this.class_message).clinic;
      ClinicQueues.closeClinic(clinic);

      var clients_to_disconnect = onsite_connections_by_clinic.remove(clinic);
      for (var _client : clients_to_disconnect) {
        try {
          _client.sendToClient(this.class_message);
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      }
      logInfo(String.format("Clinic closed : %s", clinic));
    } finally {
      connection_maps_lock.unlock();
    }
  }

  /** Removes client information from relevant data structures */
  public static void disconnectClient(ConnectionToClient client) {
    connection_maps_lock.lock();

    try {
      connected_desktop_users.remove((User) client.getInfo(USER_COL));

      var clinic = onsite_connections.remove(client);
      if (clinic != null) onsite_connections_by_clinic.get(clinic).remove(client);
    } finally {
      connection_maps_lock.unlock();
    }
  }
}
