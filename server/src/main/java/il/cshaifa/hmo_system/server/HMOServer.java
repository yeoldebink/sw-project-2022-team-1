package il.cshaifa.hmo_system.server;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.AdminAppointmentMessage;
import il.cshaifa.hmo_system.messages.AppointmentMessage;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.ClinicStaffMessage;
import il.cshaifa.hmo_system.messages.OnSiteEntryMessage;
import il.cshaifa.hmo_system.messages.GreenPassStatusMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.MessageType;
import il.cshaifa.hmo_system.messages.OnSiteSetAppointmentMessage;
import il.cshaifa.hmo_system.messages.ReportMessage;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage;
import il.cshaifa.hmo_system.messages.SetSpecialistAppointmentMessage;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage;
import il.cshaifa.hmo_system.server.ocsf.AbstractServer;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.server.server_handlers.HandleAdminAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleClinicMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleGreenPassStatusMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleLoginMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleOnSiteEntryMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleOnSiteSetAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleReportMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleSetAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleSetSpecialistAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleStaffAssignmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleStaffMessage;
import il.cshaifa.hmo_system.server.server_handlers.MessageHandler;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
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
      MessageHandler handler = null;

      Class<?> msg_class = msg.getClass();
      if (msg_class == AdminAppointmentMessage.class) {
        handler = new HandleAdminAppointmentMessage((AdminAppointmentMessage) msg, session);
      } else if (msg_class == AppointmentMessage.class) {
        handler = new HandleAppointmentMessage((AppointmentMessage) msg, session);
      } else if (msg_class == ClinicMessage.class) {
        handler = new HandleClinicMessage((ClinicMessage) msg, session);
      } else if (msg_class == GreenPassStatusMessage.class) {
        handler = new HandleGreenPassStatusMessage((GreenPassStatusMessage) msg, session);
      } else if (msg_class == LoginMessage.class) {
        handler = new HandleLoginMessage((LoginMessage) msg, session, client);
      } else if (msg_class == OnSiteEntryMessage.class) {
        handler = new HandleOnSiteEntryMessage((OnSiteEntryMessage) msg, session);
      } else if (msg_class == OnSiteSetAppointmentMessage.class){
        handler = new HandleOnSiteSetAppointmentMessage((OnSiteSetAppointmentMessage) msg, session);
      } else if (msg_class == ReportMessage.class) {
        handler = new HandleReportMessage((ReportMessage) msg, session);
      } else if (msg_class == SetAppointmentMessage.class) {
        handler = new HandleSetAppointmentMessage((SetAppointmentMessage) msg, session);
      } else if (msg_class == SetSpecialistAppointmentMessage.class) {
        handler =
            new HandleSetSpecialistAppointmentMessage(
                (SetSpecialistAppointmentMessage) msg, session);
      } else if (msg_class == StaffAssignmentMessage.class) {
        handler = new HandleStaffAssignmentMessage((StaffAssignmentMessage) msg, session);
      } else if (msg_class == ClinicStaffMessage.class) {
        handler = new HandleStaffMessage((ClinicStaffMessage) msg, session);
      }

      assert handler != null;
      handler.handleMessage();
      handler.message.message_type = MessageType.RESPONSE;
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
    User user = HandleLoginMessage.connectedUser(client);
    System.out.println("Client disconnected: " + user.getFirstName() + " " + user.getLastName());
    HandleLoginMessage.disconnectClient(client);

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
}
