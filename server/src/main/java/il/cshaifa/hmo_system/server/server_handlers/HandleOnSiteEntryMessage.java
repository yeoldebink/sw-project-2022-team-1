package il.cshaifa.hmo_system.server.server_handlers;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.OnSiteEntryMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleOnSiteEntryMessage extends MessageHandler {

  OnSiteEntryMessage class_message;
  // Hash map of Clinic-ID -> Staff Member Name/AppointmentType Name -> (Appointment, Number in line)
  public static HashMap<Integer, HashMap<String, LinkedList<Pair<Appointment, Integer>>>> appointments_lists;
  // Hash map of Clinic-ID -> Staff Member Name/AppointmentType Name -> next available number
  public static HashMap<Integer, HashMap<String, Integer>> next_numbers;

  public HandleOnSiteEntryMessage(OnSiteEntryMessage message, Session session) {
    super(message, session);
    this.class_message = (OnSiteEntryMessage) this.message;
    if (appointments_lists == null) {
      appointments_lists = new HashMap<>();
      next_numbers = new HashMap<>();
    }
  }

  @Override
  public void handleMessage() {
    User user = session.get(User.class, class_message.id);

    if (user != null && user.getRole().getName().equals("Patient")) {
      Patient patient = getUserPatient(user);
      class_message.patient = patient;

      class_message.belongs_to_clinic =
          patient.getHome_clinic().getName().equals(class_message.clinic.getName());

      List<Appointment> patients_appts = getPatientsNextAppointment(patient);
      if (patients_appts.size() == 0) {
        return;
      }
      Appointment patient_appt = patients_appts.get(0);

      String appointment_key;
      if (patient_appt.getStaff_member() == null) {
        appointment_key = patient_appt.getType().getName();
      } else {
        appointment_key =
            patient_appt.getStaff_member().getFirstName() + " "
                + patient_appt.getStaff_member().getLastName();
      }

      appointments_lists.putIfAbsent(class_message.clinic.getId(), new HashMap<>());
      appointments_lists.get(class_message.clinic.getId())
          .putIfAbsent(appointment_key, new LinkedList<>());
      next_numbers.putIfAbsent(class_message.clinic.getId(), new HashMap<>());
      next_numbers.get(class_message.clinic.getId())
          .putIfAbsent(appointment_key, 1);

      int next_number = next_numbers.get(class_message.clinic.getId()).get(appointment_key);
      if (LocalDateTime.now().isAfter(patient_appt.getDate()) &&
          appointments_lists.get(class_message.clinic.getId()).get(appointment_key).size() > 1){
        appointments_lists.get(class_message.clinic.getId()).
            get(appointment_key).add(1, new Pair<>(patient_appt, next_number));
      } else {
        appointments_lists.get(class_message.clinic.getId()).
            get(appointment_key).add(new Pair<>(patient_appt, next_number));
      }
      class_message.place_in_line = next_number;
      next_numbers.get(class_message.clinic.getId()).put(appointment_key, (next_number % 999) + 1);
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

    cr.select(root).where(
        cb.between(root.get("appt_date"), LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusMinutes(15)),
        cb.equal(root.get("patient"), patient),
        cb.equal(root.get("clinic"), class_message.clinic),
        cb.isNotNull(root.get("called_time"))
    );
    cr.orderBy(cb.asc(root.get("appt_date")));
    return session.createQuery(cr).getResultList();
  }

  private List<Appointment> getWaitingQueue(Clinic clinic, User staff_member) {
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);

    cr.select(root).where(
        cb.between(root.get("appt_date"), LocalDate.now().atStartOfDay(),
            LocalDateTime.now().plusMinutes(15)),
        cb.equal(root.get("staff_member"), staff_member),
        cb.equal(root.get("clinic"), clinic)
    );
    cr.orderBy(cb.asc(root.get("appt_date")));
    return session.createQuery(cr).getResultList();
  }
}
