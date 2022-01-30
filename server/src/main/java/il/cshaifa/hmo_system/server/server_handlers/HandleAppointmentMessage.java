package il.cshaifa.hmo_system.server.server_handlers;

import static il.cshaifa.hmo_system.Constants.APPT_DATE_COL;
import static il.cshaifa.hmo_system.Constants.CLINIC_COL;
import static il.cshaifa.hmo_system.Constants.FUTURE_APPT_CUTOFF_WEEKS;
import static il.cshaifa.hmo_system.Constants.LOCK_TIME_COL;
import static il.cshaifa.hmo_system.Constants.PATIENT_COL;
import static il.cshaifa.hmo_system.Constants.STAFF_MEMBER_COL;
import static il.cshaifa.hmo_system.Constants.TAKEN_COL;
import static il.cshaifa.hmo_system.Constants.TYPE_COL;
import static il.cshaifa.hmo_system.Constants.UNSTAFFED_APPT_TYPES;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.AppointmentMessage.*;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleAppointmentMessage extends MessageHandler {

  private final AppointmentMessage class_message;
  // Represented as weeks
  private final CriteriaQuery<Appointment> cr;
  private final Root<Appointment> root;

  public HandleAppointmentMessage(
      AppointmentMessage message, Session session, ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (AppointmentMessage) this.message;
    cr = cb.createQuery(Appointment.class);
    root = cr.from(Appointment.class);
  }

  @Override
  public void handleMessage() {
    logInfo(class_message.request.toString());

    switch (class_message.request) {
      case CLINIC_APPOINTMENTS:
        getClinicAppointments();
        break;

      case PATIENT_HISTORY:
        getPatientHistory();
        break;

      case PATIENT_NEXT_APPOINTMENT:
        getPatientNextAppointment();
        break;

      case STAFF_MEMBER_DAILY_APPOINTMENTS:
        getStaffDailyAppointments();
        break;

      case STAFF_FUTURE_APPOINTMENTS:
        getStaffFutureAppointments();
        break;
    }
  }

  /** Gets available appointments for any service type in clinic */
  private void getClinicAppointments() {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end =
        LocalDateTime.now().plusWeeks(FUTURE_APPT_CUTOFF_WEEKS.get(class_message.type));

    if (UNSTAFFED_APPT_TYPES.contains(class_message.type)) {
      cr.select(root)
          .where(
              cb.between(root.get(APPT_DATE_COL), start, end),
              cb.equal(root.get(TYPE_COL), class_message.type),
              cb.isFalse(root.get(TAKEN_COL)),
              cb.or(
                  cb.isNull(root.get(LOCK_TIME_COL)),
                  cb.lessThan(root.get(LOCK_TIME_COL), start),
                  cb.equal(root.get(PATIENT_COL), class_message.patient)));
    } else {
      cr.select(root)
          .where(
              cb.between(root.get(APPT_DATE_COL), start, end),
              cb.equal(root.get(TYPE_COL), class_message.type),
              cb.equal(root.get(CLINIC_COL), class_message.clinic),
              cb.isFalse(root.get(TAKEN_COL)),
              cb.or(
                  cb.isNull(root.get(LOCK_TIME_COL)),
                  cb.lessThan(root.get(LOCK_TIME_COL), start),
                  cb.equal(root.get(PATIENT_COL), class_message.patient)));
    }

    List<Appointment> all_appointments = session.createQuery(cr).getResultList();
    List<Appointment> appointments_in_work_hours = new ArrayList<>();
    for (Appointment appt : all_appointments) {
      DayOfWeek day = appt.getDate().toLocalDate().getDayOfWeek();
      List<LocalTime> clinic_hours = appt.getClinic().timeStringToLocalTimeList(day.getValue());
      for (int i = 0; i < clinic_hours.toArray().length; i += 2) {
        LocalTime open_time = clinic_hours.get(i), close_time = clinic_hours.get(i + 1);
        LocalTime appt_time = appt.getDate().toLocalTime();
        if (appt_time.isAfter(open_time.minusSeconds(1)) && appt_time.isBefore(close_time)) {
          appointments_in_work_hours.add(appt);
        }
      }
      class_message.appointments = appointments_in_work_hours;
    }
  }

  /** Get a patient's appointments past and future */
  private void getPatientHistory() {
    cr.select(root)
        .where(
            cb.equal(root.get(PATIENT_COL), class_message.patient), cb.isTrue(root.get(TAKEN_COL)));
    class_message.appointments = session.createQuery(cr).getResultList();
  }

  /** Get a patient's next appointment */
  private void getPatientNextAppointment() {
    cr.select(root)
        .where(
            cb.equal(root.get(PATIENT_COL), class_message.patient),
            cb.isTrue(root.get(TAKEN_COL)),
            cb.greaterThanOrEqualTo(root.get(APPT_DATE_COL), LocalDateTime.now()));
    cr.orderBy(cb.asc(root.get(APPT_DATE_COL)));
    class_message.appointments = new ArrayList<>();
    var lst = session.createQuery(cr).getResultList();
    class_message.appointments.add(lst.size() > 0 ? lst.get(0) : null);
  }

  /** Gets a staff members all appointments for today */
  private void getStaffDailyAppointments() {
    LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    cr.select(root)
        .where(
            cb.equal(root.get(STAFF_MEMBER_COL), class_message.user),
            cb.between(root.get(APPT_DATE_COL), start, end),
            cb.isTrue(root.get(TAKEN_COL)));
    class_message.appointments = session.createQuery(cr).getResultList();
  }

  /** Gets a staff member future appointments */
  private void getStaffFutureAppointments() {
    cr.select(root)
        .where(
            cb.equal(root.get(STAFF_MEMBER_COL), class_message.user),
            cb.greaterThanOrEqualTo(root.get(APPT_DATE_COL), LocalDateTime.now()),
            cb.equal(root.get(CLINIC_COL), class_message.clinic));
    class_message.appointments = session.createQuery(cr).getResultList();
  }
}
