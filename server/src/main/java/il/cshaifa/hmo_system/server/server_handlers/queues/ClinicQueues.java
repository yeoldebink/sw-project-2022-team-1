package il.cshaifa.hmo_system.server.server_handlers.queues;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ClinicQueues {

  public static final HashMap<String, String> queueNames;

  static {
    queueNames = new HashMap<>();
    queueNames.put("COVID Test", "Lab Tests");
    queueNames.put("COVID Vaccine", "Nurse");
    queueNames.put("Flu Vaccine", "Nurse");
    queueNames.put("Nurse", "Nurse");
    queueNames.put("Lab Tests", "Lab Tests");
    queueNames.put("Lab Technician", "Lab Tests");
  }

  private static String queueName(Appointment appointment) {
    var type_name = appointment.getType().getName();
    if (Arrays.asList("Family Doctor", "Pediatrician", "Specialist").contains(type_name)) {
      return String.format(
          "Dr. %s %s",
          appointment.getStaff_member().getFirstName(),
          appointment.getStaff_member().getLastName());
    } else {
      return queueNames.get(type_name);
    }
  }

  private static String queueName(User staff_member) {
    var role_name = staff_member.getRole().getName();
    if (staff_member.getRole().isSpecialist()
        || Arrays.asList("Family Doctor", "Pediatrician").contains(role_name)) {
      return String.format("Dr. %s %s", staff_member.getFirstName(), staff_member.getLastName());
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

  public static void disconnectClient(ConnectionToClient client) {
    clinicQueuesLock.lock();
    try {
      var appt_queue = clientQueues.remove(client);
      if (appt_queue != null) appt_queue.disconnectClient(client);
    } finally {
      clinicQueuesLock.unlock();
    }
  }

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

  public static void closeClinic(Clinic clinic) {
    clinicQueuesLock.lock();
    try {
      clinicQueues.remove(clinic);
    } finally {
      clinicQueuesLock.unlock();
    }
  }
}
