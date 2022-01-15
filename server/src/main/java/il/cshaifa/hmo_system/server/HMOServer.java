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
   * @param client  The client that request the login
   * @throws IOException              SQL exception
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

  private void handleReportMessage(ReportMessage msg, ConnectionToClient client)
      throws IOException {

    // we ALWAYS want to return a list, even if it's empty
    msg.reports = new ArrayList<DailyReport>();

    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);

    if (msg.report_type == ReportType.MISSED_APPOINTMENTS) {
      cr.select(root)
          .where(
              cb.between(root.get("appt_date"), msg.start_date, msg.end_date),
              cb.isTrue(root.get("taken")),
              root.get("clinic").in(msg.clinics),
              cb.isNull(root.get("called_time")));
    } else {
      cr.select(root)
          .where(
              cb.between(root.get("appt_date"), msg.start_date, msg.end_date),
              cb.isTrue(root.get("taken")),
              root.get("clinic").in(msg.clinics),
              cb.isNotNull(root.get("called_time")));
    }

    List<Appointment> relevant_appointments = session.createQuery(cr).getResultList();

    HashMap<LocalDate, HashMap<Clinic, DailyReport>> daily_reports_map =
        new HashMap<LocalDate, HashMap<Clinic, DailyReport>>();

    HashMap<LocalDate, HashMap<Clinic, HashMap<User, Integer>>> total_appointments =
        new HashMap<LocalDate, HashMap<Clinic, HashMap<User, Integer>>>();

    for (Appointment appt : relevant_appointments) {
      LocalDate appt_date = appt.getDate().toLocalDate();
      AppointmentType appt_type = appt.getType();
      Clinic appt_clinic = appt.getClinic();
      User appt_staff_member = appt.getStaff_member();

      if (!daily_reports_map.containsKey(appt_date)) {
        HashMap<Clinic, DailyReport> clinic_reports = new HashMap<Clinic, DailyReport>();
        daily_reports_map.put(appt_date, clinic_reports);
        if (msg.report_type == ReportType.AVERAGE_WAIT_TIMES) {
          HashMap<Clinic, HashMap<User, Integer>> clinic_total_appointments =
              new HashMap<Clinic, HashMap<User, Integer>>();
          total_appointments.put(appt_date, clinic_total_appointments);
        }
      }

      HashMap<Clinic, DailyReport> daily_clinics_reports = daily_reports_map.get(appt_date);

      if (!daily_clinics_reports.containsKey(appt_clinic)) {
        if (msg.report_type == ReportType.AVERAGE_WAIT_TIMES) {
          daily_clinics_reports.put(
              appt_clinic, new DailyAverageWaitTimeReport(appt_date.atStartOfDay(), appt_clinic));
          total_appointments.get(appt_date).put(appt_clinic, new HashMap<User, Integer>());
        } else {
          daily_clinics_reports.put(
              appt_clinic, new DailyAppointmentTypesReport(appt_date.atStartOfDay(), appt_clinic));
        }
      }

      if (msg.report_type == ReportType.AVERAGE_WAIT_TIMES) {
        int wait_time = (int) Duration.between(appt.getDate(), appt.getCalled_time()).toSeconds();
        DailyAverageWaitTimeReport report =
            (DailyAverageWaitTimeReport) daily_clinics_reports.get(appt_clinic);
        if (!report.report_data.containsKey(appt_staff_member)) {
          report.report_data.put(appt_staff_member, 0);
          total_appointments.get(appt_date).get(appt_clinic).put(appt_staff_member, 0);
        }

        report.report_data.put(
            appt_staff_member, report.report_data.get(appt_staff_member) + wait_time);

        total_appointments
            .get(appt_date)
            .get(appt_clinic)
            .put(
                appt_staff_member,
                total_appointments.get(appt_date).get(appt_clinic).get(appt_staff_member) + 1);

      } else {
        DailyAppointmentTypesReport report =
            (DailyAppointmentTypesReport) daily_clinics_reports.get(appt_clinic);
        if (!report.report_data.containsKey(appt_type)) {
          report.report_data.put(appt_type, 0);
        }
        report.report_data.put(appt_type, report.report_data.get(appt_type) + 1);
      }
    }

    if (msg.report_type == ReportType.AVERAGE_WAIT_TIMES) {
      for (LocalDate date : daily_reports_map.keySet()) {
        for (Clinic clinic : daily_reports_map.get(date).keySet()) {
          DailyAverageWaitTimeReport dailies =
              (DailyAverageWaitTimeReport) daily_reports_map.get(date).get(clinic);
          for (User staff_member : dailies.report_data.keySet()) {
            int total_appt = total_appointments.get(date).get(clinic).get(staff_member);
            int total_wait_time = dailies.report_data.get(staff_member);
            dailies.report_data.put(staff_member, total_wait_time / total_appt);
          }
        }
      }
    }
    for (LocalDate date : daily_reports_map.keySet()) {
      msg.reports.addAll(daily_reports_map.get(date).values());
    }

    msg.message_type = MessageType.RESPONSE;
    client.sendToClient(msg);
  }

  private boolean takeAppointment(Appointment appt, Patient patient) {
    // Reserve was requested after lock time has already expired
    if (appt.getPatient().getId() != patient.getId()) {
      return false;
    }
    appt.setTaken(true);
    appt.setLock_time(null);
    session.update(appt);
    return true;
  }

  private boolean lockAppointment(Appointment appt, Patient patient) {
    LocalDateTime lock_time = appt.getLock_time();

    // is it possible to lock this appointment? if not return false
    if (appt.isTaken()
        || (lock_time != null
        && LocalDateTime.now().isBefore(lock_time)
        && appt.getPatient().getId() != patient.getId())) {
      return false;
    }

    // get from db all patients locked appointments
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);
    cr.select(root)
        .where(
            cb.between(
                root.get("lock_time"), LocalDateTime.now(), LocalDateTime.now().plusMinutes(5)),
            cb.equal(root.get("patient"), patient));
    List<Appointment> users_locked_appointments = session.createQuery(cr).getResultList();

    // lock the relevant appointment
    appt.setLock_time(LocalDateTime.now().plusSeconds(330));
    appt.setPatient(patient);
    session.update(appt);

    // release the other appointments by the patient
    for (Appointment user_appt : users_locked_appointments) {
      releaseAppointment(user_appt);
    }
    return true;
  }

  private void releaseAppointment(Appointment appt) {
    appt.setLock_time(null);
    appt.setTaken(false);
    appt.setPatient(null);
    session.update(appt);
  }

  private void handleSetAppointmentMessage(SetAppointmentMessage msg, ConnectionToClient client)
      throws IOException {
    // before changing the state of the appointment, get the updated version of it
    session.flush();
    session.refresh(msg.appointment);

    if (msg.action == Action.TAKE) {
      msg.success = takeAppointment(msg.appointment, msg.patient);
    } else if (msg.action == Action.LOCK) {
      msg.success = lockAppointment(msg.appointment, msg.patient);
    } else if (msg.action == Action.RELEASE) {
      releaseAppointment(msg.appointment);
      msg.success = true;
    }
    session.flush();
    msg.message_type = MessageType.RESPONSE;
    client.sendToClient(msg);
  }

  private List<Role> getSpecialistRoleList() {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Role> cr = cb.createQuery(Role.class);
    Root<Role> root = cr.from(Role.class);
    cr.select(root).where(cb.isTrue(root.get("is_specialist")));
    return session.createQuery(cr).getResultList();
  }

  private List<Appointment> getSpecialistAppointments(Role role, Patient patient) {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);

    cr.select(root).where(
        cb.equal(root.get("patient"), patient),
        cb.equal(root.get("specialist_role_id"), role),
        cb.lessThan(root.get("appt_date"), LocalDateTime.now()),
        cb.isNotNull(root.get("called_time"))
    );
    cr.orderBy(cb.desc(root.get("appt_date")));
    List<Appointment> patient_past_appts = session.createQuery(cr).getResultList();

    List<Appointment> available_appts = new ArrayList<Appointment>();

    Set<User> doctors = new HashSet<>();
    for (Appointment appt : patient_past_appts) {
      if (!doctors.contains(appt.getStaff_member())) {
        cr.select(root).where(
            cb.equal(root.get("staff_member"), appt.getStaff_member()),
            cb.equal(root.get("specialist_role_id"), role),
            cb.between(root.get("appt_date"), LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3)),
            cb.isFalse(root.get("taken")),
            cb.or(cb.isNull(root.get("lock_time")),
                cb.lessThan(root.get("lock_time"), LocalDateTime.now()))
        );
        doctors.add(appt.getStaff_member());
        available_appts.addAll(session.createQuery(cr).getResultList());
      }
    }

    cr.select(root).where(
        cb.equal(root.get("specialist_role_id"), role),
        root.get("staff_member").in(doctors).not(),
        cb.between(root.get("appt_date"), LocalDateTime.now(), LocalDateTime.now().plusMonths(3)),
        cb.isFalse(root.get("taken")),
        cb.or(cb.isNull(root.get("lock_time")),
            cb.lessThan(root.get("lock_time"), LocalDateTime.now()))
    );

    available_appts.addAll(session.createQuery(cr).getResultList());
    return available_appts;
  }

  private void handleSetSpecialistAppointmentMessage(SetSpecialistAppointmentMessage msg,
      ConnectionToClient client)
      throws IOException {
    if (msg.action == SetSpecialistAppointmentMessage.Action.GET_ROLES) {
      msg.role_list = getSpecialistRoleList();
    } else if (msg.action == SetSpecialistAppointmentMessage.Action.GET_APPOINTMENTS) {
      msg.appointments = getSpecialistAppointments(msg.chosen_role, msg.patient);
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
      } //else if (msg_class == LoginMessage.class) {
//        handler = new handleLoginMessage((LoginMessage) msg, session);
//      } else if (msg_class == ReportMessage.class) {
//        handler = new handleReportMessage((ReportMessage) msg, session);
//      } else if (msg_class == SetAppointmentMessage.class) {
//        handler = new handleSetAppointmentMessage((SetAppointmentMessage) msg, session);
//      } else if (msg_class == SetSpecialistAppointmentMessage.class) {
//        handler = new handleSetSpecialistAppointmentMessage((SetSpecialistAppointmentMessage) msg, session);
//      } else if (msg_class == StaffAssignmentMessage.class) {
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
