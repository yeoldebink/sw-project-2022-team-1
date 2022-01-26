package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.HMOUtilities;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.DesktopLoginMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.OnSiteLoginMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleLoginMessage extends MessageHandler {
  public LoginMessage class_message;

  private static HashMap<Integer, ConnectionToClient> connected_users;
  private static HashMap<ConnectionToClient, User> connected_clients;

  private final ConnectionToClient client;

  public HandleLoginMessage(LoginMessage message, Session session, ConnectionToClient client) {
    super(message, session);
    this.class_message = (LoginMessage) this.message;
    this.client = client;

    if (connected_clients == null) connected_clients = new HashMap<>();
    if (connected_users == null) connected_users = new HashMap<>();
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

          default:
            var clinics = employeeClinics(user);
            if (!is_desktop) {
              class_message.user = user;
              ((OnSiteLoginMessage) this.class_message).authorized = clinics.contains(((OnSiteLoginMessage) this.class_message).clinic);
            } else if (user.getRole().getName().equals("Clinic Manager")) {
              class_message.user = user;
              ((DesktopLoginMessage) this.class_message).employee_clinics = clinics;
            }
            break;
        }

        if (is_desktop && connected_users.containsKey(user.getId())) {
          ((DesktopLoginMessage) this.class_message).already_logged_in = true;
        } else {
          connected_users.put(user.getId(), client);
          connected_clients.put(client, user);
        }
      }
    }
  }

  private List<Clinic> employeeClinics(User user) {
    CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);
    Root<ClinicStaff> root = cr.from(ClinicStaff.class);
    cr.select(root.get("clinic")).where(cb.equal(root.get("user"), user));
    return session.createQuery(cr).getResultList();
  }

  private Patient getUserPatient(User user) {
    CriteriaQuery<Patient> cr = cb.createQuery(Patient.class);
    Root<Patient> root = cr.from(Patient.class);
    cr.select(root).where(cb.equal(root.get("user"), user));
    return session.createQuery(cr).getResultList().get(0);
  }

  public static void disconnectClient(ConnectionToClient client) {
    var user = connected_clients.remove(client);
    connected_users.remove(user.getId());
  }

  public static User connectedUser(ConnectionToClient client) {
    return connected_clients.get(client);
  }
}
