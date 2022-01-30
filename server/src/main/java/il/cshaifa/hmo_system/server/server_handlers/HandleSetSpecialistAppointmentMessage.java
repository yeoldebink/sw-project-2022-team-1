package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.SetSpecialistAppointmentMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

import static il.cshaifa.hmo_system.Constants.APPT_DATE_COL;
import static il.cshaifa.hmo_system.Constants.CALLED_TIME_COL;
import static il.cshaifa.hmo_system.Constants.IS_SPECIALIST_COL;
import static il.cshaifa.hmo_system.Constants.LOCK_TIME_COL;
import static il.cshaifa.hmo_system.Constants.PATIENT_COL;
import static il.cshaifa.hmo_system.Constants.SPECIALIST_ROLE_COL;
import static il.cshaifa.hmo_system.Constants.TAKEN_COL;

public class HandleSetSpecialistAppointmentMessage extends MessageHandler {


  private final SetSpecialistAppointmentMessage class_message;

  public HandleSetSpecialistAppointmentMessage(
      SetSpecialistAppointmentMessage message, Session session,
      ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (SetSpecialistAppointmentMessage) this.message;
  }

  @Override
  public void handleMessage() {
    switch (class_message.request) {
      case GET_ROLES:
        getSpecialistRoleList();
        break;

      case GET_APPOINTMENTS:
        getSpecialistAppointments();
        break;
    }

    logSuccess(class_message.request.toString());
  }

  private void getSpecialistRoleList() {
    CriteriaQuery<Role> cr = cb.createQuery(Role.class);
    Root<Role> root = cr.from(Role.class);
    cr.select(root).where(cb.isTrue(root.get(IS_SPECIALIST_COL)));
    class_message.role_list = session.createQuery(cr).getResultList();
  }

  @SuppressWarnings("ComparatorMethodParameterNotUsed")
  private void getSpecialistAppointments() {
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);

    cr.select(root)
        .where(
            cb.equal(root.get(PATIENT_COL), class_message.patient),
            cb.equal(root.get(SPECIALIST_ROLE_COL), class_message.chosen_role),
            cb.lessThan(root.get(APPT_DATE_COL), LocalDateTime.now()),
            cb.isNotNull(root.get(CALLED_TIME_COL)));
    cr.orderBy(cb.desc(root.get(APPT_DATE_COL)));
    List<Appointment> patient_past_appts = session.createQuery(cr).getResultList();

    HashMap<User, LocalDateTime> doctors = new HashMap<>();

    for (Appointment appt : patient_past_appts) {
      doctors.putIfAbsent(appt.getStaff_member(), appt.getDate());
      if (appt.getDate().isAfter(doctors.get(appt.getStaff_member())))
        doctors.put(appt.getStaff_member(), appt.getDate());
    }

    cr.select(root)
        .where(
            cb.equal(root.get(SPECIALIST_ROLE_COL), class_message.chosen_role),
            cb.between(
                root.get(APPT_DATE_COL),
                LocalDateTime.now(),
                LocalDateTime.now()
                    .plusWeeks(HandleAppointmentMessage.max_future_appointments.get("Specialist"))),
            cb.isFalse(root.get(TAKEN_COL)),
            cb.or(
                cb.isNull(root.get(LOCK_TIME_COL)),
                cb.lessThan(root.get(LOCK_TIME_COL), LocalDateTime.now()),
                cb.and(
                    cb.isNotNull(root.get(LOCK_TIME_COL)),
                    cb.equal(root.get(PATIENT_COL), class_message.patient))));

    List<Appointment> available_appts_all = session.createQuery(cr).getResultList();

    List<Appointment> available_appts = new ArrayList<>();
    for (Appointment appt : available_appts_all) {
      DayOfWeek day = appt.getDate().toLocalDate().getDayOfWeek();
      List<LocalTime> clinic_hours = appt.getClinic().timeStringToLocalTimeList(day.getValue());
      for (int i = 0; i < clinic_hours.toArray().length; i += 2) {
        LocalTime open_time = clinic_hours.get(i), close_time = clinic_hours.get(i + 1);
        LocalTime appt_time = appt.getDate().toLocalTime();
        if (appt_time.isAfter(open_time.minusSeconds(1)) && appt_time.isBefore(close_time)) {
          available_appts.add(appt);
        }
      }
    }

    available_appts.sort(
        (appt_a, appt_b) -> {
          byte key = 0;
          key += doctors.containsKey(appt_a.getStaff_member()) ? 1 : 0;
          key += doctors.containsKey(appt_b.getStaff_member()) ? 2 : 0;
          switch (key) {
            case 1:
              return -1;
            case 2:
              return 1;
            default:
              if (key == 0 || appt_a.getStaff_member().equals(appt_b.getStaff_member()))
                return appt_a.getDate().isBefore(appt_b.getDate()) ? -1 : 1;
              else
                return doctors
                        .get(appt_a.getStaff_member())
                        .isAfter(doctors.get(appt_b.getStaff_member()))
                    ? -1
                    : 1;
          }
        });

    class_message.appointments = available_appts;
  }
}
