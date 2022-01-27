package il.cshaifa.hmo_system.server.server_handlers.queues;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

class AppointmentQueue {
  private final String name;
  private final String numberPrefix;
  private final LinkedList<QueuedAppointment> on_time;
  private final LinkedList<QueuedAppointment> late;
  private int count;
  private boolean from_late_queue;

  private final HashSet<ConnectionToClient> connected_clients;

  public AppointmentQueue(String name) {
    this.name = name;
    on_time = new LinkedList<>();
    late = new LinkedList<>();
    connected_clients = new HashSet<>();
    count = 0;

    var split = name.split(" ");
    var sbuild = new StringBuilder();
    for (var w : split) {
      sbuild.append(w.charAt(0));
    }

    numberPrefix = sbuild.toString();
  }

  public String push(Appointment appointment) {
    var num_str = String.format("%s%03d", numberPrefix, ++count);
    QueuedAppointment qappt = new QueuedAppointment(appointment, num_str);

    (appointment.getDate().isBefore(LocalDateTime.now()) ? late : on_time).addLast(qappt);
    return num_str;
  }

  public QueuedAppointment pop() {
    if (late.isEmpty() && on_time.isEmpty()) return null;
    else if (from_late_queue) {
      from_late_queue = false;
      if (late.isEmpty()) return this.pop();
      else return late.pop();
    } else {
      from_late_queue = true;
      if (on_time.isEmpty()) return this.pop();
      else return on_time.pop();
    }
  }

  public ArrayList<ConnectionToClient> getConnectedClients() {
    return new ArrayList<>(connected_clients);
  }

  public void connectClient(ConnectionToClient client) {
    connected_clients.add(client);
  }

  public void disconnectClient(ConnectionToClient client) {
    connected_clients.remove(client);
  }
}
