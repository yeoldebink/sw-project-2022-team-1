package il.cshaifa.hmo_system.server;

import il.cshaifa.hmo_system.entities.Appointment;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class App {
  private static HMOServer server;

  public static void main(String[] args) throws IOException {
    server = new HMOServer(3000);
    AppointmentReminder appointment_reminder = new AppointmentReminder();
    appointment_reminder.start();
    System.out.println("here");
    server.listen();
  }

  public static class AppointmentReminder extends Thread {
    @Override
    public void run() {
      System.out.println("in thread");
      CriteriaBuilder cb = HMOServer.session.getCriteriaBuilder();
      CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
      Root<Appointment> root = cr.from(Appointment.class);
      cr.select(root).where(
          cb.between(root.get("appt_date"), LocalDateTime.now().plusHours(23), LocalDateTime.now().plusHours(24)),
          cb.isTrue(root.get("taken"))
      );
      List<Appointment> tommorows_appts =  HMOServer.session.createQuery(cr).getResultList();
      for (Appointment appt : tommorows_appts){
        System.out.println(appt.getPatient().getUser().getFirstName());
      }
    }
  }
}
