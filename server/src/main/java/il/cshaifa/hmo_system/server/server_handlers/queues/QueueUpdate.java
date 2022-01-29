package il.cshaifa.hmo_system.server.server_handlers.queues;

import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
