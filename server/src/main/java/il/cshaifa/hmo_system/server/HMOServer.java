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

  /**
   * @param message
   * @param client
   * @throws IOException
   */
  private void handleAppointmentMessage(AppointmentMessage message, ConnectionToClient client)
      throws IOException {
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);
    LocalDateTime start, end;

    if (message.requestType == AppointmentRequestType.CLINIC_APPOINTMENTS) {

      start = LocalDateTime.now();
      end = LocalDateTime.now().plusWeeks(4);
      cr.select(root)
          .where(
              cb.between(root.get("appt_date"), start, end),
              cb.equal(root.get("type"), message.type),
              cb.equal(root.get("clinic"), message.clinic),
              cb.isFalse(root.get("taken")),
              cb.or(cb.isNull(root.get("lock_time")),
                  cb.lessThan(root.get("lock_time"), start),
                  cb.and(cb.isNotNull(root.get("lock_time")),
                      cb.equal(root.get("patient"), message.patient)))
          );
      List<Appointment> all_appts = session.createQuery(cr).getResultList();
      List<Appointment> appts_in_work_hours = new ArrayList<>();
      for (Appointment appt : all_appts) {
        DayOfWeek day = appt.getDate().toLocalDate().getDayOfWeek();
        List<LocalTime> clinic_hours = message.clinic.timeStringToLocalTime(day.getValue());
        for (int i = 0; i < clinic_hours.toArray().length; i += 2) {
          LocalTime open_time = clinic_hours.get(i), close_time = clinic_hours.get(i+1);
          LocalTime appt_time = appt.getDate().toLocalTime();
          if (appt_time.isAfter(open_time) && appt_time.isBefore(close_time)){
            appts_in_work_hours.add(appt);
          }
        }
        message.appointments = appts_in_work_hours;
      }

    } else if (message.requestType == AppointmentRequestType.STAFF_MEMBER_DAILY_APPOINTMENTS) {
      start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
      end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
      cr.select(root)
          .where(
              cb.equal(root.get("staff_member"), message.user),
              cb.between(root.get("appt_date"), start, end),
              cb.isTrue(root.get("taken")));
      message.appointments = session.createQuery(cr).getResultList();

    } else if (message.requestType == AppointmentRequestType.STAFF_FUTURE_APPOINTMENTS) {
      cr.select(root)
          .where(
              cb.equal(root.get("staff_member"), message.user),
              cb.greaterThanOrEqualTo(root.get("appt_date"), LocalDateTime.now()));
      message.appointments = session.createQuery(cr).getResultList();
    } else if (message.requestType == AppointmentRequestType.PATIENT_HISTORY) {
      cr.select(root)
          .where(
              cb.equal(root.get("patient"), message.patient),
              cb.isTrue(root.get("taken")));
      message.appointments = session.createQuery(cr).getResultList();
    }

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

  /**
   * @param entity_list Entities to be updated to DB
   */
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
   * @param client  The client that made the request
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

  private void handleAdminAppointmentMessage(AdminAppointmentMessage msg, ConnectionToClient client)
      throws IOException {
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

      if (msg.start_datetime.isAfter(LocalDateTime.now())) {
        cr.select(root)
            .where(
                cb.equal(root.get("staff_member"), msg.staff_member),
                cb.between(
                    root.get("appt_date"), msg.start_datetime, end_datetime.plusSeconds(-1)));

        // if this staff member already has appointments at these times, reject
        if (session.createQuery(cr).getResultList().size() > 0) {
          msg.type = AdminAppointmentMessageType.REJECT;
          msg.rejectionType = RejectionType.OVERLAPPING;
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
                    null,
                    null,
                    false);
            session.save(appt);
            session.flush();

            current_datetime = current_datetime.plusMinutes(len);
          }

          msg.type = AdminAppointmentMessageType.ACCEPT;
        }
      } else {
        msg.type = AdminAppointmentMessageType.REJECT;
        msg.rejectionType = RejectionType.IN_THE_PAST;
      }
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

    // get from db all patients locked appointments (besides maybe this one)
    // "locked" here meaning "locked by me BUT not taken by me"
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Appointment> cr = cb.createQuery(Appointment.class);
    Root<Appointment> root = cr.from(Appointment.class);
    cr.select(root)
        .where(
            cb.equal(root.get("patient"), patient),
            cb.equal(root.get("taken"), false),
            cb.notEqual(root, appt));
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
      } else if (msg_class == ReportMessage.class) {
        handleReportMessage((ReportMessage) msg, client);
      } else if (msg_class == SetAppointmentMessage.class) {
        handleSetAppointmentMessage((SetAppointmentMessage) msg, client);
      } else if (msg_class == SetSpecialistAppointmentMessage.class) {
        handleSetSpecialistAppointmentMessage((SetSpecialistAppointmentMessage) msg, client);
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
