package il.ac.haifa.client_server.entities.src.main.java.il.cshaifa.OCSFHmo.entities;
import il.ac.haifa.client_server.entities.src.main.java.il.cshaifa.OCSFHmo.entities.Clinic;

import java.io.Serializable;
import java.util.List;
import java.util.Comparator;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;


public class ServerFunctionalities implements Serializable {
  private static Session session;

  public enum ClientMessage {
    GET_CLINIC_LIST,
    GET_CLINIC,
    UPDATE_CLINIC_HOURS
  }


  public static SessionFactory getSessionFactory() throws HibernateException {
    Configuration configuration = new Configuration();
    ServiceRegistry serviceRegistry =
        new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
    return configuration.buildSessionFactory(serviceRegistry);
  }

  /**
   *
   * @return a list of all Clinic objects.
   * @throws Exception
   */
  public static Object GetClinicList() throws Exception {
    List<Clinic> data = null;
    try {
      SessionFactory sessionFactory = getSessionFactory();
      session = sessionFactory.openSession();
      session.beginTransaction();
      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<Clinic> query = builder.createQuery(Clinic.class);
      query.from(Clinic.class);
      data = session.createQuery(query).getResultList();
      session.flush();
      session.getTransaction().commit();
      data.sort(Comparator.comparing(Clinic::getName));
      return data;
    } catch (Exception exception) {
      if (session != null) session.getTransaction().rollback();
      System.err.println("An error occurred, changes have been rolled back.");
      exception.printStackTrace();
    } finally {
      if (session != null) session.close();
    }
    return data;
  }

  /**
   *
   * @param clinic_id - SQL identifier of clinic
   * @return - clinic object with same ID
   */
  public static Clinic GetClinic(int clinic_id) {
    try {
      SessionFactory sessionFactory = getSessionFactory();
      session = sessionFactory.openSession();
      session.beginTransaction();
      Clinic data = session.load(Clinic.class, clinic_id);
      session.flush();
      session.getTransaction().commit();
      return data;
    } catch (Exception exception) {
      if (session != null) session.getTransaction().rollback();
      System.err.println("An error occurred, changes have been rolled back.");
      exception.printStackTrace();
    } finally {
      if (session != null) session.close();
    }
    return null;
  }

  /**
   *
   * @param clinic_id - the clinic we want to change the opening hour
   * @param day - 1/2/../7 by day we want to change
   * @param workHours - String of the new opening hours. e.g. "08:00-10:00 12:00-14:00"
   * @return will return the updated Clinic object
   */
  public Clinic UpdateClinicWorkHours(int clinic_id, int day, String workHours) {
    Clinic result = null;
    try {
      SessionFactory sessionFactory = getSessionFactory();
      session = sessionFactory.openSession();
      session.beginTransaction();
      Clinic current_clinic = (Clinic) session.get(Clinic.class, clinic_id);
      current_clinic.setClinicWorkHours(day, workHours);
      session.update(current_clinic);
      session.flush();
      session.getTransaction().commit();
      result = current_clinic;
    } catch (Exception exception) {
      if (session != null) session.getTransaction().rollback();
      System.err.println("An error occurred, changes have been rolled back.");
      exception.printStackTrace();
    } finally {
      if (session != null) session.close();
    }
    return result;
  }
}
