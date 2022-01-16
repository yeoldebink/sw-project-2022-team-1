package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.SetSpecialistAppointmentMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class handleSetSpecialistAppointmentMessage extends MessageHandler {

  private final SetSpecialistAppointmentMessage class_message;
  public handleSetSpecialistAppointmentMessage(SetSpecialistAppointmentMessage message, Session session) {
    super(message, session);
    this.class_message = (SetSpecialistAppointmentMessage) this.message;
  }

  @Override
  public void handleMessage() {
    if (class_message.action == SetSpecialistAppointmentMessage.Action.GET_ROLES) {
      getSpecialistRoleList();
    } else if (class_message.action == SetSpecialistAppointmentMessage.Action.GET_APPOINTMENTS) {
      getSpecialistAppointments();
    }
  }

  private void getSpecialistRoleList() {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Role> cr = cb.createQuery(Role.class);
    Root<Role> root = cr.from(Role.class);
    cr.select(root).where(cb.isTrue(root.get("is_specialist")));
    class_message.role_list = session.createQuery(cr).getResultList();
  }

  private void getSpecialistAppointments() {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);

    cr.select(root).where(
        cb.equal(root.get("patient"), class_message.patient),
        cb.equal(root.get("specialist_role_id"), class_message.chosen_role),
        cb.lessThan(root.get("appt_date"), LocalDateTime.now()),
        cb.isNotNull(root.get("called_time"))
    );
    cr.orderBy(cb.desc(root.get("appt_date")));
    List<Appointment> patient_past_appts = session.createQuery(cr).getResultList();

    HashMap<User, LocalDateTime> doctors = new HashMap<>();

    for (Appointment appt : patient_past_appts) {
      doctors.putIfAbsent(appt.getStaff_member(), appt.getDate());
      if (appt.getDate().isAfter(doctors.get(appt.getStaff_member())))
        doctors.put(appt.getStaff_member(), appt.getDate());
    }

    cr.select(root).where(cb.equal(root.get("specialist_role_id"), class_message.chosen_role),
        cb.greaterThan(root.get("appt_date"), LocalDateTime.now()),
        cb.isFalse(root.get("taken")),
        cb.or(cb.isNull(root.get("lock_time")),
            cb.lessThan(root.get("lock_time"), LocalDateTime.now()),
            cb.and(cb.isNotNull(root.get("lock_time")),
                cb.equal(root.get("patient"), class_message.patient))));

    List<Appointment> available_appts = session.createQuery(cr).getResultList();

    available_appts.sort(
        (appt_a, appt_b) -> {
          byte key = 0;
          key += doctors.containsKey(appt_a.getStaff_member()) ? 1 : 0;
          key += doctors.containsKey(appt_b.getStaff_member()) ? 2 : 0;
          switch (key) {
            case 1:
              return 0;
            case 2:
              return 1;
            default:
              if (key == 0 || appt_a.getStaff_member().equals(appt_b.getStaff_member()))
                return appt_a.getDate().isBefore(appt_b.getDate()) ? 0 : 1;
              else return doctors.get(appt_a.getStaff_member()).isAfter(doctors.get(appt_b.getStaff_member())) ? 0 : 1;
          }
        }
    );

    class_message.appointments = available_appts;
  }
}