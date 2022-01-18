package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.HMOUtilities;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.OnSiteEntryMessage;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleOnSiteEntryMessage extends MessageHandler {

  OnSiteEntryMessage class_message;

  public HandleOnSiteEntryMessage(OnSiteEntryMessage message, Session session) {
    super(message, session);
    this.class_message = (OnSiteEntryMessage) this.message;

  }

  @Override
  public void handleMessage() {
    User user = session.get(User.class, class_message.id);

    if (user != null && user.getRole().getName().equals("Patient") && checkPassword(user)) {
      Patient patient = getUserPatient(user);
      class_message.patient = patient;

      class_message.belongs_to_clinic =
          patient.getHome_clinic().getName().equals(class_message.clinic.getName());

      List<Appointment> patients_appts = getPatientsNextAppointment(patient);
      if (patients_appts.size() == 0) return;
      Appointment patient_appt = patients_appts.get(0);
      class_message.next_appointment = patient_appt;

      List<Appointment> waiting_queue = getWaitingQueue(patient_appt.getClinic(), patient_appt.getStaff_member());
      for (int place=0; place<waiting_queue.size(); place++){
        Appointment appt = waiting_queue.get(place);
        if (appt.getCalled_time() == null && appt.getPatient().getId() == patient.getId()){

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

  private boolean checkPassword(User user){
    String user_encoded_password = user.getPassword();
    try {
      return user_encoded_password.equals(HMOUtilities.encodePassword(class_message.password, user.getSalt()));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return false;
  }

  public List<Appointment> getPatientsNextAppointment(Patient patient){
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);

    cr.select(root).where(
        cb.between(root.get("appt_date"), LocalDate.now().atStartOfDay(), LocalDateTime.now().plusMinutes(15)),
        cb.equal(root.get("patient"), patient),
        cb.equal(root.get("clinic"), class_message.clinic),
        cb.isNotNull(root.get("called_time"))
    );
    cr.orderBy(cb.asc(root.get("appt_date")));
    return session.createQuery(cr).getResultList();
  }

  private List<Appointment> getWaitingQueue(Clinic clinic, User staff_member) {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);

    cr.select(root).where(
        cb.between(root.get("appt_date"), LocalDate.now().atStartOfDay(), LocalDateTime.now().plusMinutes(15)),
        cb.equal(root.get("staff_member"), staff_member),
        cb.equal(root.get("clinic"), clinic)
    );
    cr.orderBy(cb.asc(root.get("appt_date")));
    return session.createQuery(cr).getResultList();
  }
}
