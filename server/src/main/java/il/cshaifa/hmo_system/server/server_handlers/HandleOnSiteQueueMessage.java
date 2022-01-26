package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.messages.Message;
import il.cshaifa.hmo_system.messages.OnSiteQueueMessage;
import il.cshaifa.hmo_system.server.ClinicQueues;
import java.time.LocalDateTime;
import org.hibernate.Session;

public class HandleOnSiteQueueMessage extends MessageHandler {
  OnSiteQueueMessage class_message;

  public HandleOnSiteQueueMessage(Message msg, Session session) {
    super(msg, session);
    class_message = (OnSiteQueueMessage) this.message;
  }

  @Override
  public void handleMessage() {
    switch (class_message.action) {
      case PUSH:
        createAndPushAppointment();
        break;
      case POP:
        pop();
        break;
    }
  }

  private void createAndPushAppointment() {
    Appointment appointment =
        new Appointment(
            class_message.patient,
            class_message.appt_type,
            null,
            null,
            class_message.clinic,
            LocalDateTime.now().minusSeconds(30),
            null,
            null,
            true);

    session.save(appointment);
    session.flush();

    class_message.number_in_line = ClinicQueues.push(appointment);
  }

  private void pop() {
    var q_appt = ClinicQueues.pop(class_message.staff_member, class_message.clinic);
    if (q_appt != null) {
      class_message.appointment = q_appt.appointment;
      class_message.number_in_line = q_appt.place;
    }
  }
}
