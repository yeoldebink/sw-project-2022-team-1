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
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage.RejectionType;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.AppointmentMessage.AppointmentRequestType;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.ClinicStaffMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.MessageType;
import il.cshaifa.hmo_system.messages.ReportMessage;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage.Action;
import il.cshaifa.hmo_system.messages.SetSpecialistAppointmentMessage;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage.Type;
import il.cshaifa.hmo_system.reports.DailyAppointmentTypesReport;
import il.cshaifa.hmo_system.reports.DailyAverageWaitTimeReport;
import il.cshaifa.hmo_system.reports.DailyReport;
import il.cshaifa.hmo_system.server.ocsf.AbstractServer;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.server.server_handlers.MessageHandler;
import il.cshaifa.hmo_system.server.server_handlers.handleAdminAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.handleAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.handleClinicMessage;
import il.cshaifa.hmo_system.server.server_handlers.handleLoginMessage;
import il.cshaifa.hmo_system.server.server_handlers.handleReportMessage;
import il.cshaifa.hmo_system.server.server_handlers.handleSetAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.handleSetSpecialistAppointmentMessage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
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
              if (l.size() > 0) {
                session.delete(l.get(0));
              }
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

  /**
   * See documentation for entities.Request for defined behavior.
   *
   * @param msg    the message sent.
   * @param client the connection connected to the client that sent the message.
   */
  @Override
  protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
    try {
      session = getSessionFactory().openSession();
      session.beginTransaction();
      MessageHandler handler = null;

      Class<?> msg_class = msg.getClass();
      if (msg_class == AdminAppointmentMessage.class) {
        handler = new handleAdminAppointmentMessage((AdminAppointmentMessage) msg, session);
      } else if (msg_class == AppointmentMessage.class) {
        handler = new handleAppointmentMessage((AppointmentMessage) msg, session);
      } else if (msg_class == ClinicMessage.class) {
        handler = new handleClinicMessage((ClinicMessage) msg, session);
      } else if (msg_class == LoginMessage.class) {
        handler = new handleLoginMessage((LoginMessage) msg, session);
      } else if (msg_class == ReportMessage.class) {
        handler = new handleReportMessage((ReportMessage) msg, session);
      } else if (msg_class == SetAppointmentMessage.class) {
        handler = new handleSetAppointmentMessage((SetAppointmentMessage) msg, session);
      } else if (msg_class == SetSpecialistAppointmentMessage.class) {
        handler = new handleSetSpecialistAppointmentMessage((SetSpecialistAppointmentMessage) msg, session);
      } //else if (msg_class == StaffAssignmentMessage.class) {
//        handler = new handleStaffAssignmentMessage((StaffAssignmentMessage) msg, session);
//      } else if (msg_class == ClinicStaffMessage.class) {
//        handler = new handleStaffMessage((ClinicStaffMessage) msg, session);
//      }

      handler.handleMessage();
      handler.message.message_type = MessageType.RESPONSE; // move to class
      client.sendToClient(handler.message);

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
