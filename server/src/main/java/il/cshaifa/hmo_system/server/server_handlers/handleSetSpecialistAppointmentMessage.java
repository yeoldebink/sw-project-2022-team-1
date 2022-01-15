package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.SetSpecialistAppointmentMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    List<Appointment> available_appts = new ArrayList<Appointment>();

    Set<User> doctors = new HashSet<>();
    for (Appointment appt : patient_past_appts) {
      if (!doctors.contains(appt.getStaff_member())) {
        cr.select(root).where(
            cb.equal(root.get("staff_member"), appt.getStaff_member()),
            cb.equal(root.get("specialist_role_id"), class_message.chosen_role),
            cb.between(root.get("appt_date"), LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3)),
            cb.isFalse(root.get("taken")),
            cb.or(cb.isNull(root.get("lock_time")),
                cb.lessThan(root.get("lock_time"), LocalDateTime.now()))
        );
        doctors.add(appt.getStaff_member());
        available_appts.addAll(session.createQuery(cr).getResultList());
      }
    }

    cr.select(root).where(
        cb.equal(root.get("specialist_role_id"), class_message.chosen_role),
        root.get("staff_member").in(doctors).not(),
        cb.between(root.get("appt_date"), LocalDateTime.now(), LocalDateTime.now().plusMonths(3)),
        cb.isFalse(root.get("taken")),
        cb.or(cb.isNull(root.get("lock_time")),
            cb.lessThan(root.get("lock_time"), LocalDateTime.now()))
    );

    available_appts.addAll(session.createQuery(cr).getResultList());
    class_message.appointments = available_appts;
  }
}
