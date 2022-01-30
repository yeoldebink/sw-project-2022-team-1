package il.cshaifa.hmo_system.server.server_handlers;

import static il.cshaifa.hmo_system.Constants.PATIENT_COL;
import static il.cshaifa.hmo_system.Constants.TAKEN_COL;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.UnresolvableObjectException;

public class HandleSetAppointmentMessage extends MessageHandler {
  public SetAppointmentMessage class_message;
  public String appt_comments;

  public HandleSetAppointmentMessage(
      SetAppointmentMessage message, Session session, ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (SetAppointmentMessage) this.message;
  }

  @Override
  public void handleMessage() {
    // before changing the state of the appointment, get the updated version of it
    appt_comments = class_message.appointment.getComments();
    session.flush();

    // if the patient attempted to grab a deleted appointment we reject
    try {
      session.refresh(class_message.appointment);
    } catch (UnresolvableObjectException e) {
      class_message.success = false;
      session.flush();
      return;
    }

    switch (class_message.action) {
      case LOCK:
        class_message.success = lockAppointment();
        break;

      case TAKE:
        class_message.success = takeAppointment();
        break;

      case RELEASE:
        releaseAppointment(class_message.appointment);
        class_message.success = true;
        break;
    }

    session.flush();

    if (class_message.success) {
      logSuccess(class_message.action.toString());
    } else {
      logFailure(class_message.action.toString());
    }
  }

  private boolean takeAppointment() {
    // Reserve was requested after lock time has already expired
    if (class_message.appointment.getPatient().getId() != class_message.patient.getId()) {
      return false;
    }
    class_message.appointment.setTaken(true);
    class_message.appointment.setLock_time(null);
    class_message.appointment.setComments(appt_comments);
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
            cb.isFalse(root.get(TAKEN_COL)),
            cb.equal(root.get(PATIENT_COL), class_message.patient));
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
    appt.setComments(null);
    session.update(appt);
  }
}
