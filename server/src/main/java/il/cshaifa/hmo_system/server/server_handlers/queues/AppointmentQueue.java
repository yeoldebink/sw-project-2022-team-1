package il.cshaifa.hmo_system.server.server_handlers.queues;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages a single queue in a clinic, i.e. the nurses' queue, the
 * lab techs' queue, or queues for individual doctors.
 *
 * Appointments are pushed either onto the appropriate queue (late or on-time)
 * and popped alternately from each queue.
 *
 * The queue maintains a set of clients to update on changes. This class
 * is only accessed by the ClinicQueues class, which surrounds those method
 * calls with ReentrantLocks to prevent race conditions from destroying
 * data structures.
 */
class AppointmentQueue {
  private final String name;
  private final String numberPrefix;
  private final LinkedList<QueuedAppointment> on_time;
  private final LinkedList<QueuedAppointment> late;
  private int count;
  private boolean pop_from_late_queue;

  private final HashSet<ConnectionToClient> connected_clients;

  private final HashSet<Patient> patients_in_queue;

  public AppointmentQueue(String name) {
    this.name = name;
    on_time = new LinkedList<>();
    late = new LinkedList<>();
    connected_clients = new HashSet<>();
    patients_in_queue = new HashSet<>();
    count = 0;

    var split = name.split(" ");
    var sbuild = new StringBuilder();
    for (var w : split) {
      sbuild.append(w.charAt(0));
    }

    numberPrefix = sbuild.toString();
  }

  public QueuedAppointment push(Appointment appointment) {

    if (!patients_in_queue.add(appointment.getPatient())) return null;

    var num_str = String.format("%s%03d", numberPrefix, ++count);
    QueuedAppointment qappt = new QueuedAppointment(appointment, num_str);

    (appointment.getDate().isBefore(LocalDateTime.now()) ? late : on_time).addLast(qappt);
    return qappt;
  }

  public QueuedAppointment pop() {
    var q_appt = _pop();
    if (q_appt != null) patients_in_queue.remove(q_appt.appointment.getPatient());
    return q_appt;
  }

  private QueuedAppointment _pop() {
    if (late.isEmpty() && on_time.isEmpty()) return null;
    else if (pop_from_late_queue) {
      pop_from_late_queue = false;
      if (late.isEmpty()) return this.pop();
      else return late.pop();
    } else {
      pop_from_late_queue = true;
      if (on_time.isEmpty()) return this.pop();
      else return on_time.pop();
    }
  }

  /**
   * Returns a list of clients registered to this queue
   * @return
   */
  public ArrayList<ConnectionToClient> getConnectedClients() {
    return new ArrayList<>(connected_clients);
  }

  /**
   * Registers a client to this queue
   * @param client
   */
  public void connectClient(ConnectionToClient client) {
    connected_clients.add(client);
  }

  /**
   * Unregisters a client from this queue
   * @param client
   */
  public void disconnectClient(ConnectionToClient client) {
    connected_clients.remove(client);
  }

  /**
   * Returns a linked list representing the queue in the order in which
   * the appointments will be popped; this is for presentation to the
   * staff member.
   * @return
   */
  public List<QueuedAppointment> getAsList() {
    Iterator<QueuedAppointment> i1, i2;
    if (pop_from_late_queue) {
      i1 = late.iterator();
      i2 = on_time.iterator();
    } else {
      i1 = on_time.iterator();
      i2 = late.iterator();
    }

    LinkedList<QueuedAppointment> retList = new LinkedList<>();

    while (i1.hasNext() && i2.hasNext()) {
      retList.addLast(i1.next());
      retList.addLast(i2.next());
    }

    i1.forEachRemaining(retList::addLast);
    i2.forEachRemaining(retList::addLast);

    return retList;
  }
}
