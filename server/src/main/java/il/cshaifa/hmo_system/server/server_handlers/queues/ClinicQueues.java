package il.cshaifa.hmo_system.server.server_handlers.queues;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import static il.cshaifa.hmo_system.Constants.COVID_TEST;
import static il.cshaifa.hmo_system.Constants.COVID_VACCINE;
import static il.cshaifa.hmo_system.Constants.FLU_VACCINE;
import static il.cshaifa.hmo_system.Constants.LAB_TECHNICIAN;
import static il.cshaifa.hmo_system.Constants.LAB_TESTS;
import static il.cshaifa.hmo_system.Constants.NURSE;
import static il.cshaifa.hmo_system.Constants.UNSTAFFED_APPT_TYPES;
import static il.cshaifa.hmo_system.Constants.WALK_IN_ROLES;

/**
 * This class maintains maps of queues for each of the clinics and within those
 * clinics, maps of queue names to queues. It is ONLY through here that the push and
 * pop methods of the AppointmentQueue class are to be called.
 */
public class ClinicQueues {

  public static final HashMap<String, String> queueNames;

  static {
    queueNames = new HashMap<>() {{
      put(COVID_TEST, LAB_TESTS);
      put(COVID_VACCINE, NURSE);
      put(FLU_VACCINE, NURSE);
      put(NURSE, NURSE);
      put(LAB_TESTS, LAB_TESTS);
      put(LAB_TECHNICIAN, LAB_TESTS);
    }};
  }

  private static String queueName(Appointment appointment) {
    var type_name = appointment.getType().getName();
    if (!UNSTAFFED_APPT_TYPES.contains(appointment.getType())) {
      return String.format(
          "Dr. %s",
          appointment.getStaff_member().toString());
    } else {
      return queueNames.get(type_name);
    }
  }

  private static String queueName(User staff_member) {
    var role_name = staff_member.getRole().getName();
    if (!WALK_IN_ROLES.contains(staff_member.getRole())) {
      return String.format("Dr. %s", staff_member);
    } else {
      return queueNames.get(role_name);
    }
  }

  // hashes clinics to maps of queues based on type or staff member
  private static final HashMap<Clinic, HashMap<String, AppointmentQueue>> clinicQueues;

  // this maps clients to their connected queues
  private static final HashMap<ConnectionToClient, AppointmentQueue> clientQueues;

  static {
    clinicQueues = new HashMap<>();
    clientQueues = new HashMap<>();
  }

  private static String initQueue(Appointment appointment) {
    var clinic = appointment.getClinic();
    clinicQueues.putIfAbsent(clinic, new HashMap<>());

    var q_name = queueName(appointment);
    clinicQueues.get(clinic).putIfAbsent(q_name, new AppointmentQueue(q_name));

    return q_name;
  }

  private static String initQueue(User staff_member, Clinic clinic) {
    clinicQueues.putIfAbsent(clinic, new HashMap<>());
    var q_name = queueName(staff_member);

    clinicQueues.get(clinic).putIfAbsent(q_name, new AppointmentQueue(q_name));

    return q_name;
  }

  private static final ReentrantLock clinicQueuesLock;

  static {
    clinicQueuesLock = new ReentrantLock(true);
  }

  /**
   * Connects the staff member to the relevant queue and returns the current queue as a list
   *
   * @param staff_member
   * @param clinic
   * @param client
   * @return
   */
  public static QueueUpdate connectToQueue(
      User staff_member, Clinic clinic, ConnectionToClient client) {
    clinicQueuesLock.lock();
    try {
      var q_name = initQueue(staff_member, clinic);
      var appt_queue = clinicQueues.get(clinic).get(q_name);
      appt_queue.connectClient(client);
      clientQueues.put(client, appt_queue);
      return new QueueUpdate(null, appt_queue.getAsList(), null);
    } finally {
      clinicQueuesLock.unlock();
    }
  }

  /**
   * Disconnects a staff member from their queue
   * @param client
   */
  public static void disconnectClient(ConnectionToClient client) {
    clinicQueuesLock.lock();
    try {
      var appt_queue = clientQueues.remove(client);
      if (appt_queue != null) appt_queue.disconnectClient(client);
    } finally {
      clinicQueuesLock.unlock();
    }
  }

  /**
   * Pushes an appointment onto the relevant queue in the relevant clinic
   * @param appointment
   * @return
   */
  public static QueueUpdate push(Appointment appointment) {
    clinicQueuesLock.lock();

    try {
      var q_name = initQueue(appointment);
      var appt_queue = clinicQueues.get(appointment.getClinic()).get(q_name);
      var q_appt = appt_queue.push(appointment);
      return new QueueUpdate(q_appt, appt_queue.getAsList(), appt_queue.getConnectedClients());
    } finally {
      clinicQueuesLock.unlock();
    }
  }

  /**
   * Pops an appointment from the queue to which this client is connected
   * @param client
   * @return
   */
  public static QueueUpdate pop(ConnectionToClient client) {
    clinicQueuesLock.lock();

    try {
      var appt_queue = clientQueues.get(client);
      var q_appt = appt_queue.pop();
      return new QueueUpdate(q_appt, appt_queue.getAsList(), appt_queue.getConnectedClients());
    } finally {
      clinicQueuesLock.unlock();
    }
  }

  /**
   * Closes all the queues of the given clinic
   * @param clinic
   */
  public static void closeClinic(Clinic clinic) {
    clinicQueuesLock.lock();
    try {
      clinicQueues.remove(clinic);
    } finally {
      clinicQueuesLock.unlock();
    }
  }
}
