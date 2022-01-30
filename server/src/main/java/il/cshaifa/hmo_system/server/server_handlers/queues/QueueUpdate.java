package il.cshaifa.hmo_system.server.server_handlers.queues;

import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all the data relevant to a queue update operation:
 * the popped or pushed appointment, the updated queue, a list of clients
 * which need to receive updates about this queue, and a timestamp of
 * when this update took place.
 */
public class QueueUpdate {
  public QueuedAppointment q_appt;
  public List<QueuedAppointment> updated_queue;
  public ArrayList<ConnectionToClient> clients_to_update;
  public LocalDateTime timestamp;

  public QueueUpdate(
      QueuedAppointment q_appt,
      List<QueuedAppointment> update_queue,
      ArrayList<ConnectionToClient> clients_to_update) {
    this.q_appt = q_appt;
    this.updated_queue = update_queue;
    this.clients_to_update = clients_to_update;
    timestamp = LocalDateTime.now();
  }
}
