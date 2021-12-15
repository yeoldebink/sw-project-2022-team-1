package il.cshaifa.hmo_system.server;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.Request;
import il.cshaifa.hmo_system.entities.Response;
import il.cshaifa.hmo_system.entities.Response.ResponseType;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.entities.Warning;
import il.cshaifa.hmo_system.server.ocsf.AbstractServer;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.io.IOException;
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

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Required argument: <port>");
    } else {
      HMOServer server = new HMOServer(Integer.parseInt(args[0]));
      server.listen();
    }
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
      Request req = (Request) msg;
      session = getSessionFactory().openSession();
      session.beginTransaction();
      if (req.isUpdate()) { // update the entity
        try {
          session.save(req.getEntity());
          session.flush();
          // TODO: commit
          try {
            client.sendToClient(new Response(ResponseType.CONFIRM_UPDATE, true, null));
          } catch (IOException ioException) {
            System.out.println(
                "IOException while sending update affirmation message to client: " + ioException);
          }

        } catch (HibernateException hibernateException) {
          try {
            client.sendToClient(
                new Warning("HibernateException encountered during update: " + hibernateException));
          } catch (IOException ioException) {
            System.out.println(
                "IOException while sending warning message to client: " + ioException);
          }
        }

      } else { // select all from the entity table
        Object entity = req.getEntity();
        var cb = session.getCriteriaBuilder();
        CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);
        Root<Clinic> root = cr.from(Clinic.class);
        cr.select(root);

        Query<Clinic> query = session.createQuery(cr);
        List<Clinic> results = query.getResultList();

        for (Clinic c : results) {
          System.out.println(c.getName());
        }

        try {
          client.sendToClient(new Response(ResponseType.QUERY_RESULTS, true, results));
        } catch (IOException ioException) {
          System.out.println("IOException while sending warning message to client: " + ioException);
        }
      }
      session.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  protected synchronized void clientDisconnected(ConnectionToClient client) {
    // TODO Auto-generated method stub

    System.out.println("Client Disconnected.");
    super.clientDisconnected(client);
  }

  @Override
  protected void clientConnected(ConnectionToClient client) {
    super.clientConnected(client);
    System.out.println("Client connected: " + client.getInetAddress());
  }
}
