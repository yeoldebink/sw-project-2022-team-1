package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.CommonEnums.AddAppointmentRejectionReason;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.RequestType;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleAdminAppointmentMessage extends MessageHandler {
  private final AdminAppointmentMessage class_message;
  private static Map<String, Long> appointment_duration;
  private final CriteriaQuery<Appointment> cr;
  private final Root<Appointment> root;

  private final static Logger LOGGER = Logger.getLogger(HandleAdminAppointmentMessage.class.getSimpleName());

  public HandleAdminAppointmentMessage(AdminAppointmentMessage message, Session session,
      ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (AdminAppointmentMessage) this.message;
    if (appointment_duration == null) {
      appointment_duration = new HashMap<>();
      appointment_duration.put("Family Doctor", 15L);
      appointment_duration.put("Pediatrician", 15L);
      appointment_duration.put("Specialist", 20L);
      appointment_duration.put("COVID Test", 10L);
      appointment_duration.put("COVID Vaccine", 10L);
      appointment_duration.put("Flu Vaccine", 10L);
    }
    cr = cb.createQuery(Appointment.class);
    root = cr.from(Appointment.class);
  }

  @Override
  public void handleMessage() {
    if (class_message.request == RequestType.CREATE) {
      // check if the requested appointments aren't in the past
      if (!class_message.start_datetime.isAfter(LocalDateTime.now())) {
        class_message.success = false;
        class_message.reject = AddAppointmentRejectionReason.IN_THE_PAST;
        return;
      }

      // If staff_member is null, open clinic services (Vaccine, COVID test)
      // else, open doctor's appointments
      if (class_message.staff_member == null) {
        openClinicServices();
      } else {
        openDoctorsAppointments();
      }
    } else if (class_message.request == RequestType.DELETE) {
      deleteAppointments();
    }

    if (class_message.success) {
      logSuccess();
    } else {
      logFailure(class_message.reject.toString());
    }
  }

  private void openClinicServices() {
    // calculate total time of appointments sequence
    long duration = appointment_duration.get(class_message.appt_type.getName());
    long total_minutes = class_message.count * duration;
    LocalDateTime end_datetime = class_message.start_datetime.plusMinutes(total_minutes);

    cr.select(root)
        .where(
            cb.equal(root.get("clinic"), class_message.clinic),
            cb.equal(root.get("type").get("name"), class_message.appt_type.getName()),
            cb.between(
                root.get("appt_date"), class_message.start_datetime, end_datetime.minusSeconds(1)));
    if (session.createQuery(cr).getResultList().size() > 0) {
      class_message.success = false;
      class_message.reject = AddAppointmentRejectionReason.OVERLAPPING;
      return;
    }

    LocalDateTime current_datetime = LocalDateTime.from(class_message.start_datetime);
    List<Appointment> new_appointments = new ArrayList<>();
    DayOfWeek day = class_message.start_datetime.toLocalDate().getDayOfWeek();
    List<LocalTime> opening_hours = class_message.clinic.timeStringToLocalTimeList(day.getValue());

    while (current_datetime.isBefore(end_datetime)) {
      Appointment appt = null;
      for (int i = 0; i < opening_hours.size(); i += 2) {
        LocalTime open_time = opening_hours.get(i).minusSeconds(1);
        LocalTime close_time = opening_hours.get(i + 1);
        if (current_datetime.toLocalTime().isAfter(open_time)
            && current_datetime.toLocalTime().isBefore(close_time)) {
          appt =
              new Appointment(
                  null,
                  class_message.appt_type,
                  null,
                  null,
                  class_message.clinic,
                  current_datetime,
                  null,
                  null,
                  false,
                  false);
        }
      }
      if (appt == null) {
        class_message.success = false;
        class_message.reject = AddAppointmentRejectionReason.CLINIC_CLOSED;
        logInfo("Appointments outside clinic hours");
        return;
      }
      new_appointments.add(appt);
      current_datetime = current_datetime.plusMinutes(duration);
    }

    saveEntities(new_appointments);
    class_message.success = true;
  }

  private void openDoctorsAppointments() {
    // calculate total time of appointments sequence
    long duration = appointment_duration.get(class_message.appt_type.getName());
    long total_minutes = class_message.count * duration;
    LocalDateTime end_datetime = class_message.start_datetime.plusMinutes(total_minutes);

    cr.select(root)
        .where(
            cb.equal(root.get("staff_member"), class_message.staff_member),
            cb.between(
                root.get("appt_date"),
                class_message.start_datetime.minusMinutes(duration).plusSeconds(1),
                end_datetime.minusSeconds(1)));
    if (session.createQuery(cr).getResultList().size() > 0) {
      class_message.success = false;
      class_message.reject = AddAppointmentRejectionReason.OVERLAPPING;
      return;
    }

    LocalDateTime current_datetime = LocalDateTime.from(class_message.start_datetime);
    List<Appointment> new_appointments = new ArrayList<>();
    DayOfWeek day = class_message.start_datetime.toLocalDate().getDayOfWeek();
    List<LocalTime> opening_hours = class_message.clinic.timeStringToLocalTimeList(day.getValue());

    while (current_datetime.isBefore(end_datetime)) {
      Appointment appt = null;
      for (int i = 0; i < opening_hours.size(); i += 2) {
        LocalTime open_time = opening_hours.get(i);
        LocalTime close_time = opening_hours.get(i + 1);
        if (current_datetime.toLocalTime().isAfter(open_time.minusSeconds(1))
            && current_datetime
                .toLocalTime()
                .isBefore(close_time.minusMinutes(duration).plusSeconds(1))) {
          appt =
              new Appointment(
                  null,
                  class_message.appt_type,
                  class_message.staff_member.getRole(),
                  class_message.staff_member,
                  class_message.clinic,
                  current_datetime,
                  null,
                  null,
                  false,
                  false);
        }
      }
      if (appt == null) {
        class_message.success = false;
        class_message.reject = AddAppointmentRejectionReason.CLINIC_CLOSED;
        return;
      }
      new_appointments.add(appt);
      current_datetime = current_datetime.plusMinutes(duration);
    }

    saveEntities(new_appointments);
    class_message.success = true;
  }

  private void deleteAppointments() {
    removeEntities(class_message.appointments);
    class_message.success = true;
  }
}
