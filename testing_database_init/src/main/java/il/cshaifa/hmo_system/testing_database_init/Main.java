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
        new User(9000, "9000", "Jordan", "Sullivan", "jordan.sullivan@hmolite.com", "050-7892363", roles.get("HMO Manager")));

    users.put(
        "Carla Espinosa",
        new User(1618, "1618", "Carla", "Espinosa", "carla.espinisa@hmolite.com", "050-1239537", roles.get("Clinic Manager")));

    users.put(
        "Carmen Sandiego",
        new User(8793, "8793", "Carmen", "Sandiego", "carmen.sandiego@hmolite.com", "052-4569851", roles.get("Clinic Manager")));

    users.put(
        "Billy Crystal",
        new User(5487, "5487", "Billy", "Crystal", "billy.crystal@hmolite.com", "052-1569745", roles.get("Clinic Manager")));

    users.put(
        "Joan Rivers",
        new User(1979, "1979", "Joan", "Rivers", "joan.rivers@hmolite.com", "053-9634562", roles.get("Clinic Manager")));

    users.put(
        "Bob Kelso",
        new User(3141, "3141", "Bob", "Kelso", "bob.kelso@hmolite.com", "053-1234865", roles.get("Family Doctor")));

    users.put(
        "Alice Kelso",
        new User(2659, "2659", "Alice", "Kelso", "alice.kelso@hmolite.com", "053-8987566", roles.get("Family Doctor")));

    users.put(
        "John Dorian",
        new User(5123, "5123", "John", "Dorian", "john.dorian@hmolite.com", "054-1231239", roles.get("Pediatrician")));

    users.put(
        "Dana Biton-Tishbi",
        new User(9643, "9643", "Dana", "Biton-Tishbi", "dana.bitontishbi@hmolite.com", "050-4569638", roles.get("Pediatrician")));

    users.put(
        "Perry Cox",
        new User(1499, "1499", "Perry", "Cox", "perry.cox@hmolite.com", "050-7894562", roles.get("Endocrinologist")));

    users.put(
        "Christopher Turk",
        new User(8561, "8561", "Christopher", "Turk", "christopher.turk@hmolite.com", "052-1235686", roles.get("Neurologist")));

    users.put(
        "Elliot Reed",
        new User(7893, "7893", "Elliot", "Reed", "elliot.reed@hmolite.com", "052-4569637", roles.get("Neurologist")));

    users.put(
        "Jurgen Norbert Klopp",
        new User(1901, "1901", "Jurgen Norbert", "Klopp", "jurgen.klopp@hmolite.com", "053-7897412", roles.get("Orthopedist")));

    users.put(
        "Sarah Tizdale",
        new User(5973, "5973", "Sarah", "Tizdale", "sarah.tizdale@hmolite.com", "053-8522581", roles.get("Nurse")));

    users.put(
        "Summer Smith",
        new User(2697, "2697", "Summer", "Smith", "summer.smith@hmolite.com", "053-8522521", roles.get("Nurse")));

    users.put(
        "LaVerne Roberts",
        new User(4532, "4532", "LaVerne", "Roberts", "laverne.roberts@hmolite.com", "054-7894569", roles.get("Nurse")));

    users.put(
        "Glen Matthews",
        new User(2887, "2887", "Glen", "Matthews", "glen.matthews@hmolite.com", "054-1256874", roles.get("Lab Technician")));

    users.put(
        "Franklin Kurosawa",
        new User(1967, "1967", "Franklin", "Kurowasa", "franklin.kurowasa@hmolite.com", "050-7531596", roles.get("Lab Technician")));

    users.put(
        "Avi Ron",
        new User(459721591, "459721591", "Avi", "Ron", "kingAvi@bmail.com", "050-1452365", roles.get("Patient")));

    users.put(
        "Tyler Durden",
        new User(254789321, "254789321", "Tyler", "Durden", "tyler.durden@bmail.com", "055-4264910", roles.get("Patient")));

    users.put(
        "Marquis De Carabas",
        new User(985241266, "985241266", "Marquis", "De Carabas", "mdCara@bmail.com", "058-2570022", roles.get("Patient")));

    users.put(
        "Jill Tracy",
        new User(309827022, "309827022", "Jill", "Tracy", "queenJill123@bmail.com", null, roles.get("Patient")));

    users.put(
        "Emma Tracy",
        new User(120427022, "120427022", "Emma", "Tracy", "emma.tracy@bmail.com", "050-8745613", roles.get("Patient")));

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
    clinicStaff.add(new ClinicStaff(carmel, users.get("Summer Smith")));
    clinicStaff.add(new ClinicStaff(carmel, users.get("Perry Cox")));
    clinicStaff.add(new ClinicStaff(carmel, users.get("Christopher Turk")));
    clinicStaff.add(new ClinicStaff(carmel, users.get("Glen Matthews")));

    var dizengoff = clinics.get("Dizengoff");
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("Elliot Reed")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("LaVerne Roberts")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("Christopher Turk")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("Franklin Kurosawa")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("Jurgen Norbert Klopp")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("Alice Kelso")));
    clinicStaff.add(new ClinicStaff(dizengoff, users.get("Dana Biton-Tishbi")));

    var mile_end = clinics.get("Mile End");
    clinicStaff.add(new ClinicStaff(mile_end, users.get("Perry Cox")));
    clinicStaff.add(new ClinicStaff(mile_end, users.get("Elliot Reed")));
    clinicStaff.add(new ClinicStaff(mile_end, users.get("Christopher Turk")));
    clinicStaff.add(new ClinicStaff(mile_end, users.get("Jurgen Norbert Klopp")));

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

    patients.put(
        "Emma Tracy",
        new Patient(
            users.get("Emma Tracy"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(1980, 12, 12, 21, 12)));

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

    // --------------------------- COVID Appointments --------------------------- //
    appointments.add(
        new Appointment(
            patients.get("Marquis De Carabas"),
            appt_types.get("COVID Vaccine"),
            null,
            null,
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 20, 15, 0),
            null,
            null,
            true,
            false));

    appointments.add(
        new Appointment(
            patients.get("Marquis De Carabas"),
            appt_types.get("COVID Vaccine"),
            null,
            null,
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 23, 10, 0),
            LocalDateTime.of(2022, 1, 23, 9, 50),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Avi Ron"),
            appt_types.get("COVID Test"),
            null,
            null,
            clinics.get("Dizengoff"),
            LocalDateTime.of(2022, 1, 30, 18, 0),
            LocalDateTime.of(2022, 1, 30, 18, 7, 2),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Emma Tracy"),
            appt_types.get("COVID Vaccine"),
            null,
            null,
            clinics.get("Carmel Center"),
            LocalDateTime.of(2021, 3, 11, 20, 0),
            LocalDateTime.of(2021, 3, 11, 20, 0, 59),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Jill Tracy"),
            appt_types.get("COVID Test"),
            null,
            null,
            clinics.get("Carmel Center"),
            LocalDateTime.of(2021, 3, 11, 19, 40),
            LocalDateTime.of(2021, 3, 11, 19, 41, 36),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Emma Tracy"),
            appt_types.get("COVID Vaccine"),
            null,
            null,
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 30, 18, 0),
            null,
            null,
            true,
            false));

    appointments.add(
        new Appointment(
            patients.get("Jill Tracy"),
            appt_types.get("COVID Test"),
            null,
            null,
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 30, 18, 30),
            null,
            null,
            true,
            false));

    // --------------------------- Jill Tracy Appointments --------------------------- //
    appointments.add(
        new Appointment(
            patients.get("Jill Tracy"),
            appt_types.get("Pediatrician"),
            null,
            users.get("John Dorian"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 23, 10, 0),
            LocalDateTime.of(2022, 1, 23, 10, 15),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Jill Tracy"),
            appt_types.get("Pediatrician"),
            null,
            users.get("John Dorian"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 25, 8, 30),
            LocalDateTime.of(2022, 1, 25, 8, 32, 7),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Jill Tracy"),
            appt_types.get("Specialist"),
            roles.get("Orthopedist"),
            users.get("Jurgen Norbert Klopp"),
            clinics.get("Dizengoff"),
            LocalDateTime.of(2022, 1, 30, 18, 0),
            LocalDateTime.of(2022, 1, 30, 18, 3, 52),
            null,
            true,
            true));

    // --------------------------- Emma Tracy Appointments --------------------------- //
    appointments.add(
        new Appointment(
            patients.get("Emma Tracy"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Elliot Reed"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2021, 7, 18, 9, 40),
            LocalDateTime.of(2021, 7, 18, 9, 40,16),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Emma Tracy"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Christopher Turk"),
            clinics.get("Dizengoff"),
            LocalDateTime.of(2022, 1, 23, 13, 20),
            null,
            null,
            true,
            false));

    appointments.add(
        new Appointment(
            patients.get("Emma Tracy"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Christopher Turk"),
            clinics.get("Dizengoff"),
            LocalDateTime.of(2022, 1, 27, 13, 20),
            LocalDateTime.of(2022, 1, 27, 13, 8,59),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Emma Tracy"),
            appt_types.get("Family Doctor"),
            null,
            users.get("Bob Kelso"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 30, 13, 20),
            LocalDateTime.of(2022, 1, 30, 13, 25,1),
            null,
            true,
            true));

    // --------------------------- Avi Ron Appointments --------------------------- //
    appointments.add(
        new Appointment(
            patients.get("Avi Ron"),
            appt_types.get("Family Doctor"),
            null,
            users.get("Bob Kelso"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 20, 11, 40),
            LocalDateTime.of(2022, 1, 20, 11, 41, 43),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Avi Ron"),
            appt_types.get("Specialist"),
            roles.get("Orthopedist"),
            users.get("Jurgen Norbert Klopp"),
            clinics.get("Dizengoff"),
            LocalDateTime.of(2022, 1, 22, 8, 40),
            LocalDateTime.of(2022, 1, 22, 8, 40),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Avi Ron"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Christopher Turk"),
            clinics.get("Mile End"),
            LocalDateTime.of(2022, 1, 22, 20, 20),
            LocalDateTime.of(2022, 1, 22, 20, 20, 23),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Avi Ron"),
            appt_types.get("Specialist"),
            roles.get("Endocrinologist"),
            users.get("Perry Cox"),
            clinics.get("Mile End"),
            LocalDateTime.of(2022, 1, 25, 21, 0),
            LocalDateTime.of(2022, 1, 25, 21, 2, 36),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Avi Ron"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Elliot Reed"),
            clinics.get("Mile End"),
            LocalDateTime.of(2022, 1, 26, 8, 0),
            null,
            null,
            true,
            false));

    appointments.add(
        new Appointment(
            patients.get("Avi Ron"),
            appt_types.get("Nurse"),
            roles.get("Nurse"),
            users.get("Sarah Tizdale"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 26, 13, 0),
            LocalDateTime.of(2022, 1, 26, 13, 20, 11),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Avi Ron"),
            appt_types.get("Flu Vaccine"),
            roles.get("Nurse"),
            users.get("Sarah Tizdale"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 26, 15, 0),
            LocalDateTime.of(2022, 1, 26, 15, 1, 39),
            null,
            true,
            true));

    // --------------------------- Marquis De Carabas Appointments --------------------------- //
    appointments.add(
        new Appointment(
            patients.get("Marquis De Carabas"),
            appt_types.get("Family Doctor"),
            null,
            users.get("Alice Kelso"),
            clinics.get("Dizengoff"),
            LocalDateTime.of(2022, 1, 23, 10, 30),
            LocalDateTime.of(2022, 1, 23, 10, 25),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Marquis De Carabas"),
            appt_types.get("Family Doctor"),
            null,
            users.get("Alice Kelso"),
            clinics.get("Dizengoff"),
            LocalDateTime.of(2022, 1, 30, 8, 30),
            LocalDateTime.of(2022, 1, 30, 8, 28),
            null,
            true,
            true));

    // --------------------------- Tyler Durden Appointments --------------------------- //
    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Family Doctor"),
            null,
            users.get("Bob Kelso"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 24, 12, 15),
            LocalDateTime.of(2022, 1, 24, 12, 22, 41),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Family Doctor"),
            null,
            users.get("Bob Kelso"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2022, 1, 30, 16, 45),
            LocalDateTime.of(2022, 1, 30, 16, 43, 26),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Elliot Reed"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2020, 1, 10, 12, 20),
            LocalDateTime.of(2020, 1, 10, 12, 22),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Elliot Reed"),
            clinics.get("Carmel Center"),
            LocalDateTime.of(2021, 1, 10, 14, 40),
            LocalDateTime.of(2021, 1, 10, 14, 42),
            null,
            true,
            true));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Elliot Reed"),
            clinics.get("Mile End"),
            LocalDateTime.of(2022, 1, 10, 8, 0),
            null,
            null,
            true,
            false));

    appointments.add(
        new Appointment(
            patients.get("Tyler Durden"),
            appt_types.get("Specialist"),
            roles.get("Neurologist"),
            users.get("Christopher Turk"),
            clinics.get("Mile End"),
            LocalDateTime.of(2022, 1, 23, 13, 0),
            LocalDateTime.of(2021, 1, 23, 13, 1,1),
            null,
            true,
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
