package il.cshaifa.hmo_system.testing_database_init;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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

    configuration.addAnnotatedClass(Appointment.class);
    configuration.addAnnotatedClass(AppointmentType.class);
    configuration.addAnnotatedClass(Clinic.class);
    configuration.addAnnotatedClass(ClinicStaff.class);
    configuration.addAnnotatedClass(Patient.class);
    configuration.addAnnotatedClass(Role.class);
    configuration.addAnnotatedClass(User.class);

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
        new User(9000, null, "Jordan", "Sullivan", null, null, roles.get("HMO Manager")));
    users.put(
        "Carla Espinosa",
        new User(1618, null, "Carla", "Espinosa", null, null, roles.get("Clinic Manager")));
    users.put(
        "Carmen Sandiego",
        new User(8793, null, "Carmen", "Sandiego", null, null, roles.get("Clinic Manager")));
    users.put(
        "Billy Crystal",
        new User(5487, null, "Billy", "Crystal", null, null, roles.get("Clinic Manager")));
    users.put(
        "Joan Rivers",
        new User(1979, null, "Joan", "Rviers", null, null, roles.get("Clinic Manager")));

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

  public static void main(String[] args) throws NoSuchAlgorithmException {
    try {
      session = getSessionFactory().openSession();
      session.beginTransaction();
      var roles = createRoles();
      var users = createUsers(roles);
      var clinics = createClinics(users);
      session.getTransaction().commit();
    } catch (Exception e) {
      session.getTransaction().rollback();
      e.printStackTrace();
    }
    session.close();
  }
}
