package il.cshaifa.hmo_system.testing_database_init;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class Main {

  private static Session session;

  private static SessionFactory getSessionFactory() throws HibernateException {
    Configuration configuration = new Configuration();

    configuration.addAnnotatedClass(Role.class);
    configuration.addAnnotatedClass(User.class);
    configuration.addAnnotatedClass(Clinic.class);
    configuration.addAnnotatedClass(ClinicStaff.class);
    configuration.addAnnotatedClass(Patient.class);
    configuration.addAnnotatedClass(AppointmentType.class);
    configuration.addAnnotatedClass(Appointment.class);

    ServiceRegistry serviceRegistry =
        new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
    return configuration.buildSessionFactory(serviceRegistry);
  }

  private static Map<String, Role> createRoles() {
    String[] nonSpecialistRoleNames = {
      "HMO Manager",
      "Clinic Manager",
      "Family Doctor",
      "Pediatrician",
      "Nurse",
      "Lab Technician",
      "Patient"
    };
    String[] specialistRoleNames = {
      "Cardiologist", "Endocrinologist", "Neurologist", "Orthopedist", "Dermatologist"
    };

    Map<String, Role> roles = new HashMap<>();
    for (var name : nonSpecialistRoleNames) {
      roles.put(name, new Role(name, false));
    }

    for (var name : specialistRoleNames) {
      roles.put(name, new Role(name, true));
    }

    for (var role : roles.entrySet()) {
      session.save(role.getValue());
      session.flush();
    }

    return roles;
  }

  private static Map<String, User> createUsers(Map<String, Role> roles)
      throws NoSuchAlgorithmException {
    Map<String, User> users = new HashMap<>();
    users.put(
        "Jordan Sullivan",
        new User(9000, "null", "Jordan", "Sullivan", null, null, roles.get("HMO Manager")));
    users.put(
        "Carla Espinosa",
        new User(1618, "QUEEN_carla", "Carla", "Espinosa", null, null, roles.get("Clinic Manager")));
    users.put(
        "Carmen Sandiego",
        new User(8793, null, "Carmen", "Sandiego", null, null, roles.get("Clinic Manager")));
    users.put(
        "Billy Crystal",
        new User(5487, null, "Billy", "Crystal", null, null, roles.get("Clinic Manager")));
    users.put(
        "Joan Rivers",
        new User(1979, null, "Joan", "Rivers", null, null, roles.get("Clinic Manager")));

    users.put(
        "Bob Kelso", new User(3141, null, "Bob", "Kelso", null, null, roles.get("Family Doctor")));

    users.put(
        "Elliot Reed",
        new User(7893, null, "Elliot", "Reed", null, null, roles.get("Neurologist")));

    users.put(
        "John Dorian",
        new User(5123, null, "John", "Dorian", null, null, roles.get("Pediatrician")));

    users.put(
        "Sarah Tizdale", new User(5973, null, "Sarah", "Tizdale", null, null, roles.get("Nurse")));

    users.put(
        "LaVerne Roberts",
        new User(4532, null, "LaVerne", "Roberts", null, null, roles.get("Nurse")));

    users.put(
        "Perry Cox",
        new User(1499, null, "Perry", "Cox", null, null, roles.get("Endocrinologist")));

    users.put(
        "Christopher Turk",
        new User(8561, null, "Christopher", "Turk", null, null, roles.get("Neurologist")));

    users.put(
        "Glen Matthews",
        new User(2887, null, "Glen", "Matthews", null, null, roles.get("Lab Technician")));

    users.put(
        "Franklin Kurosawa",
        new User(1967, null, "Franklin", "Kurowasa", null, null, roles.get("Lab Technician")));

    users.put("Avi Ron", new User(459721591, null, "Avi", "Ron", null, null, roles.get("Patient")));

    users.put(
        "Tyler Durden",
        new User(254789321, "password", "Tyler", "Durden", null, null, roles.get("Patient")));

    users.put(
        "Marquis De Carabas",
        new User(985241266, null, "Marquis", "De Carabas", null, null, roles.get("Patient")));

    users.put(
        "Jill Tracy", new User(309827022, null, "Jill", "Tracy", null, null, roles.get("Patient")));

    for (var user : users.entrySet()) {
      session.save(user.getValue());
      session.flush();
    }

    return users;
  }

  private static Map<String, Clinic> createClinics(Map<String, User> users) {
    Map<String, Clinic> clinics = new HashMap<>();
    clinics.put(
        "Carmel Center",
        new Clinic(
            users.get("Carla Espinosa"),
            "Carmel Center",
            "HaNassi 45, Haifa",
            "8:00-16:00",
            "8:00-16:00",
            "8:00-12:00, 16:00-20:00",
            "8:00-16:00",
            "8:00-16:00",
            "8:00-12:00",
            null));
    clinics.put(
        "Dizengoff",
        new Clinic(
            users.get("Carmen Sandiego"),
            "Dizengoff",
            "Dizengoff 12, Tel Aviv",
            "8:00-16:00",
            "8:00-12:00, 16:00-20:00",
            "8:00-16:00",
            "8:00-16:00",
            "8:00-16:00",
            "8:00-12:00",
            null));
    clinics.put(
        "Mile End",
        new Clinic(
            users.get("Billy Crystal"),
            "Mile End",
            "Mile End 27, Edinburgh",
            "8:00-14:00",
            "8:00-14:00",
            "8:00-14:00",
            "8:00-14:00",
            "8:00-14:00",
            "8:00-14:00",
            null));
    clinics.put(
        "Sacred Heart",
        new Clinic(
            users.get("Joan Rivers"),
            "Sacred Heart",
            "Muriel 18, Miami, FL",
            "10:00-18:00",
            "10:00-18:00",
            "10:00-18:00",
            "10:00-18:00",
            "10:00-18:00",
            null,
            null));

    for (var clinic : clinics.entrySet()) {
      session.save(clinic.getValue());
      session.flush();
    }

    return clinics;
  }

  public static List<ClinicStaff> assignStaff(
      Map<String, User> users, Map<String, Clinic> clinics) {
    ArrayList<ClinicStaff> clinicStaff = new ArrayList<>();

    var carmel = clinics.get("Carmel Center");
    clinicStaff.add(new ClinicStaff(carmel, users.get("Bob Kelso")));
    clinicStaff.add(new ClinicStaff(carmel, users.get("John Dorian")));
    clinicStaff.add(new ClinicStaff(carmel, users.get("Sarah Tizdale")));
    clinicStaff.add(new ClinicStaff(carmel, users.get("Perry Cox")));
    clinicStaff.add(new ClinicStaff(carmel, users.get("Christopher Turk")));
    clinicStaff.add(new ClinicStaff(carmel, users.get("Glen Matthews")));

    var dizengoff = clinics.get("Dizengoff");
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("Elliot Reed")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("John Dorian")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("LaVerne Roberts")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("Christopher Turk")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("Franklin Kurosawa")));

    for (var cstaff : clinicStaff) {
      session.save(cstaff);
      session.flush();
    }

    return clinicStaff;
  }

  public static Map<String, Patient> createPatients(
      Map<String, User> users, Map<String, Clinic> clinics) {
    var patients = new HashMap<String, Patient>();
    patients.put(
        "Avi Ron",
        new Patient(
            users.get("Avi Ron"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(1991, 1, 6, 14, 7)));

    patients.put(
        "Tyler Durden",
        new Patient(
            users.get("Tyler Durden"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(1981, 1, 6, 14, 7)));

    patients.put(
        "Marquis De Carabas",
        new Patient(
            users.get("Marquis De Carabas"),
            clinics.get("Dizengoff"),
            LocalDateTime.of(1921, 4, 1, 17, 40)));

    patients.put(
        "Jill Tracy",
        new Patient(
            users.get("Jill Tracy"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2010, 8, 12, 2, 34)));

    for (var patient : patients.values()) {
      session.save(patient);
      session.flush();
    }

    return patients;
  }

  public static Map<String, AppointmentType> createAppointmentTypes() {
    String[] appt_type_names = {
      "Family Doctor",
      "Pediatrician",
      "Specialist",
      "COVID Test",
      "COVID Vaccine",
      "Flu Vaccine",
      "Nurse",
      "Lab Tests"
    };

    HashMap<String, AppointmentType> appt_types = new HashMap<>();
    for (var name : appt_type_names) {
      var appt_type = new AppointmentType(name);
      appt_types.put(name, appt_type);
      session.save(appt_type);
      session.flush();
    }

    return appt_types;
  }

  public static List<Appointment> createAppointments(
      Map<String, Patient> patients,
      Map<String, User> users,
      Map<String, Clinic> clinics,
      Map<String, AppointmentType> appt_types,
      Map<String, Role> roles) {
    ArrayList<Appointment> appointments = new ArrayList<>();

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Family Doctor"),
            null,
            users.get("Bob Kelso"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 2, 1, 12, 20),
            LocalDateTime.of(2022, 2, 1, 12, 22, 41),
            null,
            true));

    appointments.add(
        new Appointment(
            patients.get("Marquis De Carabas"),
            appt_types.get("Family Doctor"),
            null,
            users.get("Bob Kelso"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 2, 3, 10, 40),
            LocalDateTime.of(2022, 2, 3, 10, 45, 11),
            null,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Family Doctor"),
            null,
            users.get("Bob Kelso"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2020, 2, 1, 12, 20),
            LocalDateTime.of(2020, 2, 1, 12, 22, 41),
            null,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Elliot Reed"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2021, 1, 10, 12, 20),
            LocalDateTime.of(2021, 1, 10, 12, 22),
            null,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Elliot Reed"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2021, 1, 10, 12, 20),
            LocalDateTime.of(2021, 1, 10, 12, 22),
            null,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Elliot Reed"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2021, 1, 11, 12, 20),
            LocalDateTime.of(2021, 1, 11, 12, 22),
            null,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Christopher Turk"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2021, 1, 9, 12, 20),
            LocalDateTime.of(2021, 1, 9, 12, 22),
            null,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("COVID Vaccine"),
            null,
            users.get("LaVerne Roberts"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 9, 12, 20),
            LocalDateTime.of(2022, 1, 9, 12, 22),
            null,
            true));

    for (var appt : appointments) {
      session.save(appt);
      session.flush();
    }

    return appointments;
  }

  public static void main(String[] args) throws NoSuchAlgorithmException {
    try {
      session = getSessionFactory().openSession();
      session.beginTransaction();
      var roles = createRoles();
      var users = createUsers(roles);
      var clinics = createClinics(users);
      var clinicStaff = assignStaff(users, clinics);
      var patients = createPatients(users, clinics);
      var appointment_types = createAppointmentTypes();
      var appointments = createAppointments(patients, users, clinics, appointment_types, roles);
      session.getTransaction().commit();
    } catch (Exception e) {
      e.printStackTrace();
      session.getTransaction().rollback();
    }
    session.close();
  }
}
