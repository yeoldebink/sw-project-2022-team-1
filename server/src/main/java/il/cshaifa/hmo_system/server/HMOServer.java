package il.cshaifa.hmo_system.server;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.HMOUtilities;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.AdminAppointmentMessageType;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.AppointmentMessage.AppointmentRequestType;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.ClinicStaffMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.MessageType;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage.Type;
import il.cshaifa.hmo_system.server.ocsf.AbstractServer;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

public class HMOServer extends AbstractServer {

  private static final int VACCINE_APPT_MINUTES = 10;
  private static final int FAMILY_DOCTOR_APPT_MINUTES = 15;
  private static final int SPECIALIST_APPT_MINUTES = 20;

  private static Session session;

  public HMOServer(int port) {
    super(port);
  }

  /**
   * @return A SessionFactory for the HMO system database
   * @throws HibernateException
   */
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

  private void handleStaffMessage(ClinicStaffMessage message, ConnectionToClient client)
      throws IOException {
    var cb = session.getCriteriaBuilder();
    CriteriaQuery<ClinicStaff> cr = cb.createQuery(ClinicStaff.class);
    Root<ClinicStaff> root = cr.from(ClinicStaff.class);
    cr.select(root);
    message.staff_list = session.createQuery(cr).getResultList();

    HashSet<User> assigned_staff = new HashSet<>();
    for (var cstaff : message.staff_list) {
      assigned_staff.add(cstaff.getUser());
    }

    var cru = cb.createQuery(User.class);
    var uroot = cru.from(User.class);
    cru.select(uroot)
        .where(
            cb.notLike(uroot.get("role").get("name"), "%Manager%"),
            cb.notEqual(uroot.get("role").get("name"), "Patient"));
    var all_staff = session.createQuery(cru).getResultList();

    for (var staff_member : all_staff) {
      if (!assigned_staff.contains(staff_member)) {
        message.staff_list.add(new ClinicStaff(new Clinic(), staff_member));
      }
    }

    message.message_type = MessageType.RESPONSE;
    client.sendToClient(message);
  }

  private void handleAppointmentMessage(AppointmentMessage message, ConnectionToClient client)
      throws IOException {
    var cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);
    LocalDateTime start, end;

    if (message.requestType == AppointmentRequestType.CLINIC_APPOINTMENTS) {
      start = LocalDateTime.now();
      end = LocalDateTime.now().plusWeeks(3);
      cr.select(root)
          .where(
              cb.equal(root.get("type"), message.type),
              cb.equal(root.get("clinic"), message.clinic),
              cb.between(root.get("appt_date"), start, end),
              cb.equal(root.get("taken"), false),
              cb.greaterThanOrEqualTo(root.get("lock_time"), start.plusMinutes(5)));

    } else if (message.requestType == AppointmentRequestType.STAFF_MEMBER_DAILY_APPOINTMENTS) {
      start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
      end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
      cr.select(root)
          .where(
              cb.equal(root.get("staff_member"), message.user),
              cb.between(root.get("appt_date"), start, end),
              cb.equal(root.get("taken"), true));

    } else if (message.requestType == AppointmentRequestType.STAFF_FUTURE_APPOINTMENTS) {
      cr.select(root)
          .where(
              cb.equal(root.get("staff_member"), message.user),
              cb.greaterThanOrEqualTo(root.get("appt_date"), LocalDateTime.now()));

    } else if (message.requestType == AppointmentRequestType.PATIENT_HISTORY) {
      cr.select(root)
          .where(
              cb.equal(root.get("patient"), getUserPatient(message.user)),
              cb.equal(root.get("taken"), true));
    }

    message.appointments = session.createQuery(cr).getResultList();

    message.message_type = MessageType.RESPONSE;
    client.sendToClient(message);
  }

  /**
   * Get clinics list and send this list to client
   *
   * @param client The client that made the request
   * @throws IOException
   */
  protected void sendClinicList(ConnectionToClient client) throws IOException {
    var cb = session.getCriteriaBuilder();
    CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);
    Root<Clinic> root = cr.from(Clinic.class);
    cr.select(root);

    Query<Clinic> query = session.createQuery(cr);
    List<Clinic> results = query.getResultList();

    ClinicMessage clinics_msg = new ClinicMessage();
    clinics_msg.clinics = results;
    clinics_msg.message_type = MessageType.RESPONSE;

    client.sendToClient(clinics_msg);
  }

  /** @param entity_list Entities to be updated to DB */
  protected void updateEntities(List<?> entity_list) {
    for (var entity : entity_list) {
      session.update(entity);
      session.flush();
    }
  }

  protected void addEntities(List<?> entity_list) {
    for (var entity : entity_list) {
      session.persist(entity);
      session.flush();
    }
  }

  /**
   * If message.clinics is null, this means client requested all of the clinics else, client has
   * made changes to this clinics and apply changes to DB
   *
   * @param message ClinicMessage
   * @param client The client that made the request
   * @throws IOException SQL exception
   */
  protected void handleClinicMessage(ClinicMessage message, ConnectionToClient client)
      throws IOException {
    if (message.clinics == null) {
      sendClinicList(client);
    } else {
      updateEntities(message.clinics);
    }
  }

  protected Patient getUserPatient(User user) {
    var cb = session.getCriteriaBuilder();
    CriteriaQuery<Patient> cr = cb.createQuery(Patient.class);
    Root<Patient> root = cr.from(Patient.class);
    cr.select(root).where(cb.equal(root.get("user"), user));
    return session.createQuery(cr).getResultList().get(0);
  }

  /**
   * If login successful will send to client LoginMessage with user and his details
   *
   * @param message LoginMassage should be with user_id and password
   * @param client The client that request the login
   * @throws IOException SQL exception
   * @throws NoSuchAlgorithmException Encoding password exception
   */
  protected void handleLogin(LoginMessage message, ConnectionToClient client)
      throws IOException, NoSuchAlgorithmException {
    User user = (User) session.get(User.class, message.id);

    // prepare for the case of clinic queries
    var cb = session.getCriteriaBuilder();
    CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);

    if (user != null) {
      String user_encoded_password = user.getPassword();
      String entered_password = HMOUtilities.encodePassword(message.password, user.getSalt());
      if (user_encoded_password.equals(entered_password)) {
        message.user = user;
        if (user.getRole().getName().equals("Patient")) {
          message.patient_data = getUserPatient(user);

        } else if (user.getRole().getName().equals("Clinic Manager")) {
          Root<Clinic> root = cr.from(Clinic.class);
          cr.select(root).where(cb.equal(root.get("manager_user"), user));
          message.employee_clinics = session.createQuery(cr).getResultList();

        } else if (!user.getRole().getName().equals("HMO Manager")) {
          Root<ClinicStaff> root = cr.from(ClinicStaff.class);
          cr.select(root.get("clinic")).where(cb.equal(root.get("user"), user));
          message.employee_clinics = session.createQuery(cr).getResultList();
        }
      }
    }

    message.message_type = MessageType.RESPONSE;
    client.sendToClient(message);
  }

  private void handleStaffAssignmentMessage(StaffAssignmentMessage msg, ConnectionToClient client)
      throws IOException {

    var cb = session.getCriteriaBuilder();
    var cr = cb.createQuery(ClinicStaff.class);
    Root<ClinicStaff> root = cr.from(ClinicStaff.class);

    // create an appropriate lambda to either merge or delete rows from the ClinicStaff table
    Function<ClinicStaff, Void> session_method =
        msg.type == Type.ASSIGN
            ? cstaff -> {
              session.merge(cstaff);
              return null;
            }
            : cstaff -> {
              cr.select(root)
                  .where(
                      cb.equal(root.get("user"), cstaff.getUser()),
                      cb.equal(root.get("clinic"), cstaff.getClinic()));
              var l = session.createQuery(cr).getResultList();
              if (l.size() > 0) session.delete(l.get(0));
              return null;
            };

    for (var staff_member : msg.staff) {
      var assignment = new ClinicStaff(msg.clinic, staff_member);
      session_method.apply(assignment);
      session.flush();
    }

    msg.message_type = MessageType.RESPONSE;
    client.sendToClient(msg);
  }

  private void handleAdminAppointmentMessage(
      AdminAppointmentMessage msg, ConnectionToClient client) throws IOException {
    if (msg.type == AdminAppointmentMessageType.DELETE) {
      for (var appt : msg.appointments) {
        session.delete(appt);
        session.flush();
      }

      msg.type = AdminAppointmentMessageType.ACCEPT;
    } else if (msg.staff_member != null) { // will be null for vaccines
      // verify that this staff member has no appointments in any clinic during these times
      int len;
      Role specialist_role;
      if (msg.staff_member.getRole().isSpecialist()) {
        len = SPECIALIST_APPT_MINUTES;
        specialist_role = msg.staff_member.getRole();
      } else {
        len = FAMILY_DOCTOR_APPT_MINUTES;
        specialist_role = null;
      }

      LocalDateTime end_datetime = msg.start_datetime.plusMinutes((long) msg.count * len);

      var cb = session.getCriteriaBuilder();
      var cr = cb.createQuery(Appointment.class);
      var root = cr.from(Appointment.class);
      cr.select(root)
          .where(
              cb.equal(root.get("staff_member"), msg.staff_member),
              cb.between(root.get("appt_date"), msg.start_datetime, end_datetime));

      // if this staff member already has appointments at these times, reject
      if (session.createQuery(cr).getResultList().size() > 0) {
        msg.type = AdminAppointmentMessageType.REJECT;

      } else {
        var current_datetime = LocalDateTime.from(msg.start_datetime);
        // TODO: validate by clinic hours as well
        while (current_datetime.isBefore(end_datetime)) {
          var appt =
              new Appointment(
                  null,
                  msg.appt_type,
                  specialist_role,
                  msg.staff_member,
                  msg.clinic,
                  current_datetime,
                  null);
          session.save(appt);
          session.flush();

          current_datetime = current_datetime.plusMinutes(len);
        }

        msg.type = AdminAppointmentMessageType.ACCEPT;
      }
    }

    msg.message_type = MessageType.RESPONSE;
    client.sendToClient(msg);
  }

  /**
   * See documentation for entities.Request for defined behavior.
   *
   * @param msg the message sent.
   * @param client the connection connected to the client that sent the message.
   */
  @Override
  protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
    try {
      session = getSessionFactory().openSession();
      session.beginTransaction();

      Class<?> msg_class = msg.getClass();
      if (msg_class == ClinicMessage.class) {
        handleClinicMessage((ClinicMessage) msg, client);
      } else if (msg_class == LoginMessage.class) {
        handleLogin((LoginMessage) msg, client);
      } else if (msg_class == AppointmentMessage.class) {
        handleAppointmentMessage((AppointmentMessage) msg, client);
      } else if (msg_class == ClinicStaffMessage.class) {
        handleStaffMessage((ClinicStaffMessage) msg, client);
      } else if (msg_class == StaffAssignmentMessage.class) {
        handleStaffAssignmentMessage((StaffAssignmentMessage) msg, client);
      } else if (msg_class == AdminAppointmentMessage.class) {
        handleAdminAppointmentMessage((AdminAppointmentMessage) msg, client);
      }

      session.close();
    } catch (Exception exception) {
      exception.printStackTrace();
      if (session != null) {
        session.getTransaction().rollback();
      }
    }
  }

  @Override
  protected synchronized void clientDisconnected(ConnectionToClient client) {
    System.out.println("Client Disconnected.");
    super.clientDisconnected(client);
  }

  @Override
  protected void clientConnected(ConnectionToClient client) {
    super.clientConnected(client);
    System.out.println("Client connected: " + client.getInetAddress());
  }

  @Override
  protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
    exception.printStackTrace();
  }

  /**
   * @param args
   * @throws IOException Main server method, create server and listen to clients
   */
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Required argument: <port>");
    } else {
      HMOServer server = new HMOServer(Integer.parseInt(args[0]));
      server.listen();
    }
  }
}
