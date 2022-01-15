package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.HMOUtilities;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.LoginMessage;
import java.security.NoSuchAlgorithmException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class handleLoginMessage extends MessageHandler {
  public LoginMessage class_message;

  public handleLoginMessage(LoginMessage message, Session session) {
    super(message, session);
    this.class_message = (LoginMessage) this.message;
  }

  /** If login successful will update the LoginMessage with user and his details */
  @Override
  public void handleMessage(){
    User user = session.get(User.class, class_message.id);
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
