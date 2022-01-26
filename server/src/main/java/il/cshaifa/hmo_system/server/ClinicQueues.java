package il.cshaifa.hmo_system.server;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

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

  public static class QueuedAppointment {
    public final Appointment appointment;
    public final String place;

    public QueuedAppointment(Appointment appointment, String place) {
      this.appointment = appointment;
      this.place = place;
    }
  }

  public static class AppointmentQueue {
    private final String name;
    private final String numberPrefix;
    private final LinkedList<QueuedAppointment> on_time;
    private final LinkedList<QueuedAppointment> late;
    private int count;
    private boolean from_late_queue;

    public AppointmentQueue(String name) {
      this.name = name;
      on_time = new LinkedList<>();
      late = new LinkedList<>();
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

  static {
    clinicQueues = new HashMap<>();
  }

  private static void initQueue(Appointment appointment) {
    var clinic = appointment.getClinic();
    clinicQueues.putIfAbsent(clinic, new HashMap<>());

    var q_name = queueName(appointment);
    clinicQueues.get(clinic).putIfAbsent(q_name, new AppointmentQueue(q_name));
  }

  public static String push(Appointment appointment) {
    initQueue(appointment);
    return clinicQueues.get(appointment.getClinic()).get(queueName(appointment)).push(appointment);
  }

  public static QueuedAppointment pop(User staff_member, Clinic clinic) {
    var q_name = queueName(staff_member);
    if (!clinicQueues.containsKey(clinic) || !clinicQueues.get(clinic).containsKey(q_name)) {
      return null;
    } else {
      return clinicQueues.get(clinic).get(q_name).pop();
    }
  }

  public static void close(Clinic clinic) {
    clinicQueues.remove(clinic);
  }
}
