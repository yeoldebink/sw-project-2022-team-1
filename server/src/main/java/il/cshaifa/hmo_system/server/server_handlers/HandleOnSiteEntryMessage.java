package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.OnSiteEntryMessage;
import il.cshaifa.hmo_system.server.ClinicQueues;
import java.time.LocalDateTime;
import java.util.List;
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

    if (user != null && user.getRole().getName().equals("Patient")) {
      Patient patient = getUserPatient(user);
      class_message.patient = patient;

      List<Appointment> patients_appts = getPatientsNextAppointment(patient);
      if (patients_appts.size() == 0) {
        return;
      }
      Appointment patient_appt = patients_appts.get(0);

      patient_appt.setArrived(true);
      session.update(patient_appt);
      session.flush();

      class_message.place_in_line = ClinicQueues.push(patient_appt);
    }
  }

  private Patient getUserPatient(User user) {
    CriteriaQuery<Patient> cr = cb.createQuery(Patient.class);
    Root<Patient> root = cr.from(Patient.class);
    cr.select(root).where(cb.equal(root.get("user"), user));
    return session.createQuery(cr).getResultList().get(0);
  }

  public List<Appointment> getPatientsNextAppointment(Patient patient) {
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);

    cr.select(root)
        .where(
            cb.between(
                root.get("appt_date"),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusMinutes(15)),
            cb.equal(root.get("patient"), patient),
            cb.equal(root.get("clinic"), class_message.clinic),
            cb.isTrue(root.get("taken")),
            cb.isNull(root.get("called_time")),
            cb.isFalse(root.get("arrived")));
    cr.orderBy(cb.asc(root.get("appt_date")));
    return session.createQuery(cr).getResultList();
  }
}
