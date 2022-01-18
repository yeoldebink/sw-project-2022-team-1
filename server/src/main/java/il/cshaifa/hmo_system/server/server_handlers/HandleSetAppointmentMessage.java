package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.CommonEnums.SetAppointmentAction;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleSetAppointmentMessage extends MessageHandler {
  public SetAppointmentMessage class_message;

  public HandleSetAppointmentMessage(SetAppointmentMessage message, Session session) {
    super(message, session);
    this.class_message = (SetAppointmentMessage) this.message;
  }

  @Override
  public void handleMessage() {
    // before changing the state of the appointment, get the updated version of it
    session.flush();
    session.refresh(class_message.appointment);

    if (class_message.action == SetAppointmentAction.TAKE) {
      class_message.success = takeAppointment();
    } else if (class_message.action == SetAppointmentAction.LOCK) {
      class_message.success = lockAppointment();
    } else if (class_message.action == SetAppointmentAction.RELEASE) {
      releaseAppointment(class_message.appointment);
      class_message.success = true;
    }
    session.flush();
  }

  private boolean takeAppointment() {
    // Reserve was requested after lock time has already expired
    if (class_message.appointment.getPatient().getId() != class_message.patient.getId()) {
      return false;
    }
    class_message.appointment.setTaken(true);
    class_message.appointment.setLock_time(null);
    session.update(class_message.appointment);
    return true;
  }

  private boolean lockAppointment() {
    LocalDateTime lock_time = class_message.appointment.getLock_time();

    // is it possible to lock this appointment? if not return false
    if (class_message.appointment.isTaken()
        || (lock_time != null
            && LocalDateTime.now().isBefore(lock_time)
            && class_message.appointment.getPatient().getId() != class_message.patient.getId())) {
      return false;
    }

    // get from db all patients locked appointments
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);
    cr.select(root)
        .where(
            cb.between(
                root.get("lock_time"), LocalDateTime.now(), LocalDateTime.now().plusMinutes(5)),
            cb.equal(root.get("patient"), class_message.patient));
    List<Appointment> users_locked_appointments = session.createQuery(cr).getResultList();

    // lock the relevant appointment
    class_message.appointment.setLock_time(LocalDateTime.now().plusSeconds(330));
    class_message.appointment.setPatient(class_message.patient);
    session.update(class_message.appointment);

    // release the other appointments by the patient
    for (Appointment user_appt : users_locked_appointments) {
      releaseAppointment(user_appt);
    }
    return true;
  }

  private void releaseAppointment(Appointment appt) {
    appt.setLock_time(null);
    appt.setTaken(false);
    appt.setPatient(null);
    session.update(appt);
  }
}
