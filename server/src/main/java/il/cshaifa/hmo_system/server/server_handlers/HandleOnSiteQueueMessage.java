package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.messages.Message;
import il.cshaifa.hmo_system.messages.OnSiteQueueMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.server.server_handlers.queues.ClinicQueues;
import java.time.LocalDateTime;
import org.hibernate.Session;

public class HandleOnSiteQueueMessage extends MessageHandler {
  OnSiteQueueMessage class_message;
  private final ConnectionToClient client;

  public HandleOnSiteQueueMessage(Message msg, Session session,
      ConnectionToClient client) {
    super(msg, session);
    class_message = (OnSiteQueueMessage) this.message;
    this.client = client;
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
            HandleLoginMessage.stationClinic(client),
            LocalDateTime.now().minusSeconds(30),
            null,
            null,
            true, false);

    session.save(appointment);
    session.flush();

    class_message.q_appt = ClinicQueues.push(appointment);
    class_message.updated_queue = ClinicQueues.getQueueAsList(client);
  }

  private void pop() {
    var q_appt = ClinicQueues.pop(client);
    if (q_appt != null) {
      // set the called time
      q_appt.appointment.setCalled_time(LocalDateTime.now());
      session.update(q_appt.appointment);
      session.flush();

      class_message.q_appt = q_appt;
      class_message.updated_queue = ClinicQueues.getQueueAsList(client);
    }
  }
}
