package il.cshaifa.hmo_system.server;

import il.cshaifa.hmo_system.Utils;
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
import il.cshaifa.hmo_system.messages.GreenPassStatusMessage;
import il.cshaifa.hmo_system.messages.LoginMessage;
import il.cshaifa.hmo_system.messages.Message.MessageType;
import il.cshaifa.hmo_system.messages.OnSiteEntryMessage;
import il.cshaifa.hmo_system.messages.OnSiteQueueMessage;
import il.cshaifa.hmo_system.messages.ReportMessage;
import il.cshaifa.hmo_system.messages.SetAppointmentMessage;
import il.cshaifa.hmo_system.messages.SetSpecialistAppointmentMessage;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage;
import il.cshaifa.hmo_system.messages.UpdateAppointmentMessage;
import il.cshaifa.hmo_system.server.ocsf.AbstractServer;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import il.cshaifa.hmo_system.server.server_handlers.HandleAdminAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleClinicMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleGreenPassStatusMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleLoginMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleOnSiteEntryMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleOnSiteQueueMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleReportMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleSetAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleSetSpecialistAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleStaffAssignmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleStaffMessage;
import il.cshaifa.hmo_system.server.server_handlers.HandleUpdateAppointmentMessage;
import il.cshaifa.hmo_system.server.server_handlers.MessageHandler;
import il.cshaifa.hmo_system.server.server_handlers.queues.ClinicQueues;
import il.cshaifa.hmo_system.server.server_handlers.queues.QueueUpdate;
import java.io.EOFException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HMOServer extends AbstractServer {

  public static Session session;

  private final AppointmentReminderThread appt_reminder_thread;

  public HMOServer(int port) {
    super(port);
    appt_reminder_thread = new AppointmentReminderThread();
    appt_reminder_thread.start();
  }

  /**
   * @return A SessionFactory for the HMO system database
   * @throws HibernateException
   */
  private static SessionFactory getSessionFactory() throws HibernateException {
    java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

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
      } else if (msg instanceof LoginMessage) { // because of subclasses
        handler = new HandleLoginMessage((LoginMessage) msg, session, client);
      } else if (msg_class == OnSiteEntryMessage.class) {
        handler = new HandleOnSiteEntryMessage((OnSiteEntryMessage) msg, session, client);
      } else if (msg_class == OnSiteQueueMessage.class) {
        handler = new HandleOnSiteQueueMessage((OnSiteQueueMessage) msg, session, client);
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
      } else if (msg_class == UpdateAppointmentMessage.class) {
        handler = new HandleUpdateAppointmentMessage((UpdateAppointmentMessage) msg, session);
      }

      assert handler != null;
      handler.handleMessage();
      handler.message.message_type = MessageType.RESPONSE;
      client.sendToClient(handler.message);

      // janky, yes, but it's late and I'm tired
      QueueUpdate q_update =
          handler instanceof HandleOnSiteEntryMessage
              ? ((HandleOnSiteEntryMessage) handler).q_update
              : handler instanceof HandleOnSiteQueueMessage
                  ? ((HandleOnSiteQueueMessage) handler).q_update
                  : null;

      if (q_update != null) {
        // need to update all those clients
        var q_msg = OnSiteQueueMessage.updateMessage(q_update.updated_queue, q_update.timestamp);
        for (var _client : q_update.clients_to_update) {
          if (!_client.equals(client)) _client.sendToClient(q_msg);
        }
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
    var user_str = client.getInfo("user_str");
    System.out.printf(
        "Client disconnected: [%s] @ %s\n",
        user_str != null ? user_str : "not logged in", client.getInfo("inet"));

    HandleLoginMessage.disconnectClient(client);
    ClinicQueues.disconnectClient(client);

    super.clientDisconnected(client);
  }

  @Override
  protected void clientConnected(ConnectionToClient client) {
    super.clientConnected(client);
    client.setInfo("inet", client.getInetAddress());
    System.out.printf("Client connected: %s\n", client.getInetAddress());
  }

  @Override
  protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
    if (!(exception instanceof EOFException) || client.toString() != null) {
      exception.printStackTrace();
    }
  }

  public static class AppointmentReminderThread extends Thread {
    @Override
    public void run() {
      while (true) {
        System.out.println("Performing email reminders...");
        session = getSessionFactory().openSession();
        session.beginTransaction();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
        Root<Appointment> root = cr.from(Appointment.class);
        cr.select(root)
            .where(
                cb.between(
                    root.get("appt_date"),
                    LocalDateTime.now().plusHours(23),
                    LocalDateTime.now().plusHours(24)),
                cb.isTrue(root.get("taken")));
        List<Appointment> tommorows_appts = session.createQuery(cr).getResultList();
        session.close();

        for (Appointment appt : tommorows_appts) {
          PrepareAndSendEmail(appt);
          System.out.printf("Sent email for appointment id %s%n", appt.getId());
        }

        try {
          sleep(3600000);
        } catch (InterruptedException e) {
          return;
        }
      }
    }

    private void PrepareAndSendEmail(Appointment appt) {
      User currPatient = appt.getPatient().getUser();
      var date_str = Utils.prettifyDateTime(appt.getDate());
      var type_str =
          appt.getStaff_member() != null
              ? appt.getStaff_member().getRole().getName()
              : appt.getType().getName();

      String subject = String.format("Your %s appointment - %s", type_str, date_str);

      String bodyText =
          String.format(
              """
              Hello, %s

              This is an automated reminder that you have a %s appointment%s scheduled on %s.

              Clinic details:
              -------------------
              %s
              %s

              Please be sure to arrive at the clinic between %s and %s.

              If you wish to cancel or reschedule your appointment please do so at your earliest
              convenience via the desktop application or by phone at *2700.

              See you soon!

              ############################
              This is an automated message. Do not reply to this email.""",
              currPatient.getFirstName(),
              type_str,
              appt.getStaff_member() != null
                  ? " with Dr. " + appt.getStaff_member().toString()
                  : "",
              date_str,
              appt.getClinic().getName(),
              appt.getClinic().getAddress(),
              Utils.prettifyTime(appt.getDate().minusMinutes(15).toLocalTime()),
              Utils.prettifyTime(appt.getDate().plusHours(1).toLocalTime()));

      EmailSender.SendEmail(currPatient.getEmail(), subject, bodyText);
    }
  }

  public static class EmailSender {
    private static final String host = "***REMOVED***";
    private static final String port = "2525";
    private static final String user_name = "***REMOVED***";
    private static final String password = "***REMOVED***";
    private static final String from =
        "***REMOVED***"; // Needs to remain this email for smtp-pulse API

    public static void SendEmail(String to, String subject, String bodyText) {
      // Get system properties & setup mail server
      Properties properties = System.getProperties();
      properties.setProperty("mail.smtp.host", host);
      properties.setProperty("mail.smtp.port", port);
      properties.setProperty("mail.smtp.ssl.enable", "false");
      properties.setProperty("java.net.preferIPv4Stack", "true");
      properties.setProperty("mail.smtp.auth", "true");

      try {
        javax.mail.Session session =
            javax.mail.Session.getDefaultInstance(
                properties,
                new Authenticator() {
                  @Override
                  protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user_name, password);
                  }
                });

        // Create message using session object
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        message.setSubject(subject);
        message.setText(bodyText);

        Transport.send(message);
        System.out.println("Sent email successfully....");
      } catch (MessagingException mex) {
        mex.printStackTrace();
      }
    }
  }
}
