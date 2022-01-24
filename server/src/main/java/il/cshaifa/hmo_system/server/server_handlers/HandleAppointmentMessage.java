package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.AppointmentMessage.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleAppointmentMessage extends MessageHandler {

  private final AppointmentMessage class_message;
  // Represented as weeks
  public static Map<String, Long> max_future_appointments;
  private final CriteriaQuery<Appointment> cr;
  private final Root<Appointment> root;

  public HandleAppointmentMessage(AppointmentMessage message, Session session) {
    super(message, session);
    this.class_message = (AppointmentMessage) this.message;
    if (max_future_appointments == null) {
      max_future_appointments = new HashMap<>();
      max_future_appointments.put("Family Doctor", 4L);
      max_future_appointments.put("Pediatrician", 4L);
      max_future_appointments.put("Specialist", 12L);
      max_future_appointments.put("COVID Test", 4L);
      max_future_appointments.put("COVID Vaccine", 4L);
      max_future_appointments.put("Flu Vaccine", 4L);
    }
    cr = cb.createQuery(Appointment.class);
    root = cr.from(Appointment.class);
  }

  @Override
  public void handleMessage() {
    if (class_message.request == RequestType.CLINIC_APPOINTMENTS) {
      getClinicAppointments();
    } else if (class_message.request == RequestType.PATIENT_HISTORY) {
      getPatientHistory();
    } else if (class_message.request == RequestType.PATIENT_NEXT_APPOINTMENT) {
      getPatientNextAppointment();
    } else if (class_message.request == RequestType.STAFF_MEMBER_DAILY_APPOINTMENTS) {
      getStaffDailyAppointments();
    } else if (class_message.request == RequestType.STAFF_FUTURE_APPOINTMENTS) {
      getStaffFutureAppointments();
    }
  }

  /** Gets available appointments for any service type in clinic */
  private void getClinicAppointments() {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end =
        LocalDateTime.now().plusWeeks(max_future_appointments.get(class_message.type.getName()));
    if (class_message.type.getName().equals("COVID Test")
        || class_message.type.getName().equals("COVID Vaccine")
        || class_message.type.getName().equals("Flu Vaccine")) {
      cr.select(root)
          .where(
              cb.between(root.get("appt_date"), start, end),
              cb.equal(root.get("type"), class_message.type),
              cb.isFalse(root.get("taken")),
              cb.or(
                  cb.isNull(root.get("lock_time")),
                  cb.lessThan(root.get("lock_time"), start),
                  cb.equal(root.get("patient"), class_message.patient)));
    } else {
      cr.select(root)
          .where(
              cb.between(root.get("appt_date"), start, end),
              cb.equal(root.get("type"), class_message.type),
              cb.equal(root.get("clinic"), class_message.clinic),
              cb.isFalse(root.get("taken")),
              cb.or(
                  cb.isNull(root.get("lock_time")),
                  cb.lessThan(root.get("lock_time"), start),
                  cb.equal(root.get("patient"), class_message.patient)));
    }
    List<Appointment> all_appointments = session.createQuery(cr).getResultList();
    List<Appointment> appointments_in_work_hours = new ArrayList<>();
    for (Appointment appt : all_appointments) {
      DayOfWeek day = appt.getDate().toLocalDate().getDayOfWeek();
      List<LocalTime> clinic_hours = class_message.clinic.timeStringToLocalTimeList(day.getValue());
      for (int i = 0; i < clinic_hours.toArray().length; i += 2) {
        LocalTime open_time = clinic_hours.get(i), close_time = clinic_hours.get(i + 1);
        LocalTime appt_time = appt.getDate().toLocalTime();
        if (appt_time.isAfter(open_time) && appt_time.isBefore(close_time)) {
          appointments_in_work_hours.add(appt);
        }
      }
      class_message.appointments = appointments_in_work_hours;
    }
  }

  /** Get a patient's appointments past and future */
  private void getPatientHistory() {
    cr.select(root)
        .where(cb.equal(root.get("patient"), class_message.patient), cb.isTrue(root.get("taken")));
    class_message.appointments = session.createQuery(cr).getResultList();
  }

  /** Get a patient's next appointment */
  private void getPatientNextAppointment() {
    cr.select(root)
        .where(
            cb.equal(root.get("patient"), class_message.patient),
            cb.isTrue(root.get("taken")),
            cb.greaterThanOrEqualTo(root.get("appt_date"), LocalDateTime.now()));
    cr.orderBy(cb.asc(root.get("appt_date")));
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
            cb.equal(root.get("staff_member"), class_message.user),
            cb.between(root.get("appt_date"), start, end),
            cb.isTrue(root.get("taken")));
    class_message.appointments = session.createQuery(cr).getResultList();
  }

  /** Gets a staff member future appointments */
  private void getStaffFutureAppointments() {
    cr.select(root)
        .where(
            cb.equal(root.get("staff_member"), class_message.user),
            cb.greaterThanOrEqualTo(root.get("appt_date"), LocalDateTime.now()),
            cb.equal(root.get("clinic"), class_message.clinic));
    class_message.appointments = session.createQuery(cr).getResultList();
  }
}
