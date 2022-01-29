package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.HMOUtilities;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.DesktopLoginMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.OnSiteLoginMessage;
import il.cshaifa.hmo_system.server.server_handlers.queues.ClinicQueues;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
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
  private static final HashMap<Integer, ConnectionToClient> connected_desktop_users;
  private static final HashMap<ConnectionToClient, User> connected_desktop_clients;
  private static final HashMap<ConnectionToClient, Clinic> onsite_connections;
  private static final HashMap<Clinic, HashSet<ConnectionToClient>> onsite_connections_by_clinic;
  static {
    connection_maps_lock = new ReentrantLock(true);
    connected_desktop_users = new HashMap<>();
    connected_desktop_clients = new HashMap<>();
    onsite_connections = new HashMap<>();
    onsite_connections_by_clinic = new HashMap<>();
  }

  private final ConnectionToClient client;

  public HandleLoginMessage(LoginMessage message, Session session, ConnectionToClient client) {
    super(message, session);
    this.class_message = (LoginMessage) this.message;
    this.client = client;
  }

  public static Clinic stationClinic(ConnectionToClient client) {
    return onsite_connections.get(client);
  }

  /** If login successful will update the LoginMessage with user and his details */
  @Override
  public void handleMessage() {
    User user = session.get(User.class, class_message.id);
    CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);

    boolean is_desktop = this.class_message instanceof DesktopLoginMessage;

    if (user != null) {
      String user_encoded_password = user.getPassword();
      String entered_password = null;
      try {
        entered_password = HMOUtilities.encodePassword(class_message.password, user.getSalt());
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
      if (user_encoded_password.equals(entered_password)) {
        // patients and HMO Mgr are only able to login on the desktop client
        switch (user.getRole().getName()) {
          case "Patient":
            if (is_desktop) {
              class_message.user = user;
              ((DesktopLoginMessage) this.class_message).patient_data = getUserPatient(user);
            }
            break;

          case "HMO Manager":
            if (is_desktop) {
              class_message.user = user;
            }
            break;

          default: // this is for Clinic Mgr & clinic staff
            var clinics = employeeClinics(user);

            // all clinic staff (incl. the Clinic Manager) are allowed to log in via the on-site app
            // provided what they want to do is open the application
            if (!is_desktop && ((OnSiteLoginMessage) this.class_message).action == OnSiteLoginAction.LOGIN) {
              if (clinics.contains(((OnSiteLoginMessage) this.class_message).clinic)) {
                this.class_message.user = user;
                connectOnSiteStation();

                // on-site login for a clinic manager hinges on them being the manager of this clinic
                if (user.getRole().getName().equals("Clinic Manager")
                    && clinics.contains(((OnSiteLoginMessage) this.class_message).clinic)) {
                  switch (((OnSiteLoginMessage) this.class_message).action) {
                    case CLOSE_STATION:
                      disconnectOnSiteStation(client);
                      break;
                    case CLOSE_CLINIC:
                      closeClinic(((OnSiteLoginMessage) this.class_message).clinic);
                      break;
                    default:
                      break;
                  }
                } else {
                  // this is an employee who needs to be connected to their patient queue
                  ClinicQueues.connectToQueue(
                      user, ((OnSiteLoginMessage) this.class_message).clinic, client);
                }
              }

            } else if (user.getRole().getName().equals("Clinic Manager")) {
              // she can log in both on desktop and on-site to open stations
              if (is_desktop) {
                ((DesktopLoginMessage) this.class_message).employee_clinics = clinics;
                this.class_message.user = user;
              }
            }
            break;
        }


        if (!is_desktop) return;

        connection_maps_lock.lock();

        try {
          if(connected_desktop_users.containsKey(user.getId())) {
            ((DesktopLoginMessage) this.class_message).already_logged_in = true;
          } else {
            connected_desktop_users.put(user.getId(), client);
            connected_desktop_clients.put(client, user);
          }
        } finally {
          connection_maps_lock.unlock();
        }
      }
    }
  }

  private List<Clinic> employeeClinics(User user) {
    CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);
    if (user.getRole().getName().equals("Clinic Manager")) {
      Root<Clinic> root = cr.from(Clinic.class);
      cr.select(root).where(cb.equal(root.get("manager_user"), user));
    } else {
      Root<ClinicStaff> root = cr.from(ClinicStaff.class);
      cr.select(root.get("clinic")).where(cb.equal(root.get("user"), user));
    }
    return session.createQuery(cr).getResultList();
  }

  private Patient getUserPatient(User user) {
    CriteriaQuery<Patient> cr = cb.createQuery(Patient.class);
    Root<Patient> root = cr.from(Patient.class);
    cr.select(root).where(cb.equal(root.get("user"), user));
    return session.createQuery(cr).getResultList().get(0);
  }

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

  public static void closeClinic(Clinic clinic) {
    connection_maps_lock.lock();
    try {
      for (var client : onsite_connections_by_clinic.get(clinic)) {
        onsite_connections.remove(client);
        try {
          client.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      onsite_connections_by_clinic.remove(clinic);
      ClinicQueues.closeClinic(clinic);
    } finally {
      connection_maps_lock.unlock();
    }
  }

  public static void disconnectClient(ConnectionToClient client) {
    connection_maps_lock.lock();

    try {
      var user = connected_desktop_clients.remove(client);
      if (user != null) connected_desktop_users.remove(user.getId());

      var clinic = onsite_connections.remove(client);
      if (clinic != null) onsite_connections_by_clinic.get(clinic).remove(client);
    } finally {
      connection_maps_lock.unlock();
    }
  }

  public static User connectedUser(ConnectionToClient client) {
    connection_maps_lock.lock();
    try {
      return connected_desktop_clients.get(client);
    } finally {
      connection_maps_lock.unlock();
    }
  }
}
