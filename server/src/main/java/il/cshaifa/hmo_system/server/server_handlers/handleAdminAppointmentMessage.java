package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.AdminAppointmentMessageType;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.RejectionType;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class handleAdminAppointmentMessage extends MessageHandler {
  private final AdminAppointmentMessage class_message;
  private final Map<String, Long> appointment_duration;

  public handleAdminAppointmentMessage(AdminAppointmentMessage message, Session session) {
    super(message, session);
    this.class_message = (AdminAppointmentMessage) this.message;
    this.appointment_duration = new HashMap<>();
    this.appointment_duration.put("Family Doctor", 15L);
    this.appointment_duration.put("Pediatrician", 15L);
    this.appointment_duration.put("Specialist", 20L);
    this.appointment_duration.put("COVID Test", 10L);
    this.appointment_duration.put("COVID Vaccine", 10L);
    this.appointment_duration.put("Flu Vaccine", 10L);
  }

  @Override
  public void handleMessage() {
    if (class_message.type == AdminAppointmentMessageType.CREATE){
      // check if the requested appointments aren't in the past
      if (!class_message.start_datetime.isAfter(LocalDateTime.now())) {
        class_message.type = AdminAppointmentMessageType.REJECT;
        class_message.rejectionType = RejectionType.IN_THE_PAST;
        return;
      }

      // If staff_member is null, open clinic services (Vaccine, COVID test)
      // else, open doctors appointments
      if (class_message.staff_member == null) {
        openClinicServices();
      } else {
        openDoctorsAppointments();
      }
    } else if (class_message.type == AdminAppointmentMessageType.DELETE) {
      deleteAppointments();
    }
  }

  private void openClinicServices() {
    // calculate total time of appointments sequence
    long duration = appointment_duration.get(class_message.appt_type.getName());
    long total_minutes = class_message.count * duration;
    LocalDateTime end_datetime = class_message.start_datetime.plusMinutes(total_minutes);

    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);
    cr.select(root)
        .where(
            cb.equal(root.get("clinic"), class_message.clinic),
            cb.equal(root.get("appt_type"), class_message.appt_type),
            cb.between(root.get("appt_date"), class_message.start_datetime, end_datetime.minusSeconds(1)));
    if (session.createQuery(cr).getResultList().size() > 0) {
      class_message.type = AdminAppointmentMessageType.REJECT;
      class_message.rejectionType = RejectionType.OVERLAPPING;
      return;
    }

    LocalDateTime current_datetime = LocalDateTime.from(class_message.start_datetime);
    List<Appointment> new_appointments = new ArrayList<>();
    DayOfWeek day = class_message.start_datetime.toLocalDate().getDayOfWeek();
    List<LocalTime> opening_hours = class_message.clinic.timeStringToLocalTime(day.getValue());

    while (current_datetime.isBefore(end_datetime)) {
      Appointment appt = null;
      for (int i=0; i<opening_hours.size();i+=2) {
        LocalTime open_time = opening_hours.get(i);
        LocalTime close_time = opening_hours.get(i+1);
        if (current_datetime.toLocalTime().isAfter(open_time)
            && current_datetime.toLocalTime().isBefore(close_time)) {
          appt = new Appointment(null, class_message.appt_type,
              null, null, class_message.clinic,
              current_datetime, null, null, false);
        }
      }
      if (appt == null) {
        class_message.type = AdminAppointmentMessageType.REJECT;
        class_message.rejectionType = RejectionType.CLINIC_CLOSED;
        return;
      }
      new_appointments.add(appt);
      current_datetime = current_datetime.plusMinutes(duration);
    }

    for (Appointment appt : new_appointments) {
      session.save(appt);
    }

    session.flush();
    class_message.type = AdminAppointmentMessageType.ACCEPT;
  }

  private void openDoctorsAppointments() {
    // calculate total time of appointments sequence
    long duration = appointment_duration.get(class_message.appt_type.getName());
    long total_minutes = class_message.count * duration;
    LocalDateTime end_datetime = class_message.start_datetime.plusMinutes(total_minutes);

    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);
    cr.select(root)
        .where(
            cb.equal(root.get("clinic"), class_message.clinic),
            cb.equal(root.get("staff_member"), class_message.staff_member),
            cb.between(root.get("appt_date"), class_message.start_datetime, end_datetime.minusSeconds(1)));
    if (session.createQuery(cr).getResultList().size() > 0) {
      class_message.type = AdminAppointmentMessageType.REJECT;
      class_message.rejectionType = RejectionType.OVERLAPPING;
      return;
    }

    LocalDateTime current_datetime = LocalDateTime.from(class_message.start_datetime);
    List<Appointment> new_appointments = new ArrayList<>();
    DayOfWeek day = class_message.start_datetime.toLocalDate().getDayOfWeek();
    List<LocalTime> opening_hours = class_message.clinic.timeStringToLocalTime(day.getValue());

    while (current_datetime.isBefore(end_datetime)) {
      Appointment appt = null;
      for (int i=0; i<opening_hours.size();i+=2) {
        LocalTime open_time = opening_hours.get(i);
        LocalTime close_time = opening_hours.get(i+1);
        if (current_datetime.toLocalTime().isAfter(open_time)
            && current_datetime.toLocalTime().isBefore(close_time)) {
          appt = new Appointment(null, class_message.appt_type,
              class_message.staff_member.getRole(), class_message.staff_member,
              class_message.clinic, current_datetime, null, null, false);
        }
      }
      if (appt == null) {
        class_message.type = AdminAppointmentMessageType.REJECT;
        class_message.rejectionType = RejectionType.CLINIC_CLOSED;
        return;
      }
      new_appointments.add(appt);
      current_datetime = current_datetime.plusMinutes(duration);
    }

    for (Appointment appt : new_appointments) {
      session.save(appt);
    }

    session.flush();
    class_message.type = AdminAppointmentMessageType.ACCEPT;
  }

  private void deleteAppointments(){
    for (Appointment appt : class_message.appointments) {
      session.delete(appt);
      session.flush();
    }

    class_message.type = AdminAppointmentMessageType.ACCEPT;
  }

}
