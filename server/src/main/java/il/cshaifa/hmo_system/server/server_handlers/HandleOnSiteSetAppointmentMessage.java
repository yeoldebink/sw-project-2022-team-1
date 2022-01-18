package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.OnSiteSetAppointmentMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleOnSiteSetAppointmentMessage extends MessageHandler {

  OnSiteSetAppointmentMessage class_message;
  private static HashMap<Clinic, HashMap<User, >> connected_users;


  public HandleOnSiteSetAppointmentMessage(OnSiteSetAppointmentMessage message, Session session) {
    super(message, session);
    this.class_message = (OnSiteSetAppointmentMessage) this.message;
  }

  @Override
  public void handleMessage() {
    class_message.place_in_line = getAppointmentQueue(class_message.clinic, class_message.type);
    setAppointment(class_message.clinic, class_message.type);
  }

  private int getAppointmentQueue(Clinic clinic, AppointmentType type) {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);

    cr.select(root).where(
        cb.between(root.get("appt_date"), LocalDate.now().atStartOfDay(), LocalDateTime.now()),
        cb.equal(root.get("type"), type),
        cb.equal(root.get("clinic"), clinic)
    );
    return session.createQuery(cr).getResultList().size();
  }

  private void setAppointment(Clinic clinic, AppointmentType type) {
    Appointment appt = new Appointment(class_message.patient, type, null, null, clinic,
        LocalDateTime.now(), null, null, true);
    session.persist(appt);
    session.flush();
  }
}
