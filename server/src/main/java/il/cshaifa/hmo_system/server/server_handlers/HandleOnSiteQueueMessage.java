package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.CommonEnums.OnSiteQueueRejectionReason;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.Message;
import il.cshaifa.hmo_system.messages.OnSiteQueueMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.server.server_handlers.queues.ClinicQueues;
import il.cshaifa.hmo_system.server.server_handlers.queues.QueueUpdate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Logger;
import org.hibernate.Session;

public class HandleOnSiteQueueMessage extends MessageHandler {
  OnSiteQueueMessage class_message;
  public QueueUpdate q_update;


  public HandleOnSiteQueueMessage(Message msg, Session session, ConnectionToClient client) {
    super(msg, session, client);
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

    var now = LocalDateTime.now().toLocalTime();

    if (class_message.appt_type.getName().equals("Lab Tests")
        && (now.isBefore(LocalTime.of(8, 0)) || now.isAfter(LocalTime.of(10, 0)))) {
      this.class_message.rejection_reason = OnSiteQueueRejectionReason.OUT_OF_HOURS;
      logFailure(OnSiteQueueRejectionReason.OUT_OF_HOURS.toString());
      return;
    }

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
            true,
            true);

    session.save(appointment);
    session.flush();

    q_update = ClinicQueues.push(appointment);
    class_message.q_appt = q_update.q_appt;

    if (q_update.q_appt == null) {
      session.delete(appointment);
      session.flush();
      this.class_message.rejection_reason = OnSiteQueueRejectionReason.ALREADY_IN_QUEUE;
    } else {
      logSuccess(String.format("Entered queue: %s %s", class_message.patient.getUser(), q_update.q_appt.place_in_line));
    }
  }

  private void pop() {
    this.q_update = ClinicQueues.pop(client);
    if (q_update.q_appt != null) {
      // set the called time
      q_update.q_appt.appointment.setCalled_time(LocalDateTime.now());
      q_update.q_appt.appointment.setStaff_member((User) client.getInfo("user"));
      session.update(q_update.q_appt.appointment);
      session.flush();

      class_message.q_appt = q_update.q_appt;
      class_message.updated_queue = q_update.updated_queue;
      class_message.queue_timestamp = q_update.timestamp;

      logSuccess(String.format("Called: %s %s", q_update.q_appt.appointment.getPatient().getUser(), q_update.q_appt.place_in_line));
    }
  }
}
