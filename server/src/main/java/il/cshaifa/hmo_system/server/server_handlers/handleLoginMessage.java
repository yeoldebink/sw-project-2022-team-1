package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.HMOUtilities;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class handleLoginMessage extends MessageHandler {
  public LoginMessage class_message;
  public ConnectionToClient client;

  public static HashMap<User, ConnectionToClient> connected_users;
  public static HashMap<ConnectionToClient, User> connected_clients;

  public handleLoginMessage(LoginMessage message, Session session, ConnectionToClient client) {
    super(message, session);
    if (connected_users == null) connected_users = new HashMap<>();
    this.class_message = (LoginMessage) this.message;
    this.client = client;
  }

  /** If login successful will update the LoginMessage with user and his details */
  @Override
  public void handleMessage(){
    User user = session.get(User.class, class_message.id);

    if (connected_users.containsKey(user)) {
      class_message.already_logged_in = true;
      return;
    }

    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);

    if (user != null) {
      String user_encoded_password = user.getPassword();
      String entered_password = null;
      try {
        entered_password = HMOUtilities.encodePassword(class_message.password, user.getSalt());
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
      if (user_encoded_password.equals(entered_password)) {
        class_message.user = user;
        if (user.getRole().getName().equals("Patient")) {
          class_message.patient_data = getUserPatient(user);

        } else if (user.getRole().getName().equals("Clinic Manager")) {
          Root<Clinic> root = cr.from(Clinic.class);
          cr.select(root).where(cb.equal(root.get("manager_user"), user));
          class_message.employee_clinics = session.createQuery(cr).getResultList();

        } else if (!user.getRole().getName().equals("HMO Manager")) {
          Root<ClinicStaff> root = cr.from(ClinicStaff.class);
          cr.select(root.get("clinic")).where(cb.equal(root.get("user"), user));
          class_message.employee_clinics = session.createQuery(cr).getResultList();
        }

        connected_users.put(user, client);
        connected_clients.put(client, user);
      }
    }
  }

  private Patient getUserPatient(User user) {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Patient> cr = cb.createQuery(Patient.class);
    Root<Patient> root = cr.from(Patient.class);
    cr.select(root).where(cb.equal(root.get("user"), user));
    return session.createQuery(cr).getResultList().get(0);
  }
}
