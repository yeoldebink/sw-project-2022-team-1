package il.cshaifa.hmo_system.server;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.HMOUtilities;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.AppointmentMessage.appointmentRequest;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.messageType;
import il.cshaifa.hmo_system.messages.StaffMessage;
import il.cshaifa.hmo_system.server.ocsf.AbstractServer;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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

  private void handleStaffMessage(StaffMessage message, ConnectionToClient client)
      throws IOException {
    message.message_type = messageType.RESPONSE;
    var cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(ClinicStaff.class);
    Root<ClinicStaff> root = cr.from(ClinicStaff.class);
    cr.select(root);

    Query<ClinicStaff> query = session.createQuery(cr);

    message.staff_list = query.getResultList();
    client.sendToClient(message);
  }

  private void handleAppointmentMessage(AppointmentMessage message, ConnectionToClient client)
      throws IOException {

    message.message_type = messageType.RESPONSE;
    var cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);
    LocalDateTime start, end;
    if (message.requestType == appointmentRequest.SCHEDULE_APPOINTMENT) {
      start = LocalDateTime.now();
      end = LocalDateTime.now().plusWeeks(3);
      cr.select(root)
          .where(
              cb.equal(root.get("type"), message.type),
              cb.equal(root.get("clinic"), message.clinic),
              cb.between(root.get("appt_date"), start, end),
              cb.equal(root.get("taken"), false),
              cb.greaterThanOrEqualTo(root.get("lock_time"), start.plusMinutes(5)));

    } else if (message.requestType == appointmentRequest.SHOW_STAFF_APPOINTMENTS) {
      start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
      end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
      cr.select(root)
          .where(
              cb.equal(root.get("staff_member"), message.user),
              cb.between(root.get("appt_date"), start, end),
              cb.equal(root.get("taken"), true));

    } else if (message.requestType == appointmentRequest.SHOW_PATIENT_HISTORY) {
      cr.select(root)
          .where(
              cb.equal(root.get("patient"), getUserPatient(message.user)),
              cb.equal(root.get("taken"), true));
    } else if (message.requestType == appointmentRequest.GENERATE_APPOINTMENTS) {
      addEntities(message.appointments);
      return;
    }

    message.appointments = session.createQuery(cr).getResultList();
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
    clinics_msg.message_type = messageType.RESPONSE;

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

  protected void createEntities(List<?> entity_list) {}

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
    message.message_type = messageType.RESPONSE;
    User user = (User) session.get(User.class, message.id);
    String user_encoded_password = user.getPassword();
    String entered_password = HMOUtilities.encodePassword(message.password, user.getSalt());
    if (user_encoded_password.equals(entered_password)) {
      message.user = user;
      if (user.getRole().getName().equals("Patient")) {
        message.patient_data = getUserPatient(user);
      } else if (!user.getRole().getName().equals("HMO Manager")) {
        var cb = session.getCriteriaBuilder();
        CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);
        Root<ClinicStaff> root = cr.from(ClinicStaff.class);
        cr.select(root.get("clinic")).where(cb.equal(root.get("user"), user));
        message.employee_clinics = session.createQuery(cr).getResultList();
      }
    }
    client.sendToClient(message);
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
      } else if (msg_class == StaffMessage.class) {
        handleStaffMessage((StaffMessage) msg_class, client);
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
