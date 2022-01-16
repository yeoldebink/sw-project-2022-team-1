package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.Role;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.Message;
import il.cshaifa.hmo_system.messages.Message.MessageType;
import il.cshaifa.hmo_system.messages.ReportMessage;
import il.cshaifa.hmo_system.messages.ReportMessage.ReportType;
import il.cshaifa.hmo_system.reports.DailyAppointmentTypesReport;
import il.cshaifa.hmo_system.reports.DailyAverageWaitTimeReport;
import il.cshaifa.hmo_system.reports.DailyReport;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class handleReportMessage extends MessageHandler {
  ReportMessage class_message;
  private HashMap<LocalDate, HashMap<Clinic, DailyReport>> daily_reports_map;
  HashMap<LocalDate, HashMap<Clinic, HashMap<User, Integer>>> total_appointments_map;
  private final CriteriaBuilder cb;
  private final CriteriaQuery<Appointment> cr;
  private final Root<Appointment> root;
  private List<Appointment> relevant_appointments;
  private String[] clinics_general_services;

  public handleReportMessage(ReportMessage message, Session session) {
    super(message, session);
    this.class_message = (ReportMessage) this.message;
    cb = session.getCriteriaBuilder();
    cr = cb.createQuery(Appointment.class);
    root = cr.from(Appointment.class);
    clinics_general_services = new String[]{
        "COVID Test",
        "COVID Vaccine",
        "Flu Vaccine",
        "Nurse",
        "Lab Tests"
    };
  }

  @Override
  public void handleMessage() {
    if (class_message.report_type == ReportType.APPOINTMENT_ATTENDANCE) {
      initServicesReport();
      getAttendanceReport();
    } else if (class_message.report_type == ReportType.AVERAGE_WAIT_TIMES) {
      initStaffMemberReport();
      getAverageWaitTimeReport();
    } else if (class_message.report_type == ReportType.MISSED_APPOINTMENTS) {
      initServicesReport();
      getMissedAppointmentsReport();
    }
  }

  private void initServicesReport() {
    LocalDate current_date = class_message.start_date.toLocalDate();
    LocalDate report_end_date = class_message.end_date.toLocalDate();

    daily_reports_map = new HashMap<>();
    CriteriaQuery<ClinicStaff> cr_ClinicStaff = cb.createQuery(ClinicStaff.class);
    Root<ClinicStaff> root_ClinicStaff = cr_ClinicStaff.from(ClinicStaff.class);
    cr_ClinicStaff.select(root_ClinicStaff).where(
        root_ClinicStaff.get("clinic").in(class_message.clinics));
    List<ClinicStaff> clinics_staff = session.createQuery(cr_ClinicStaff).getResultList();

    while (!current_date.isAfter(report_end_date)) {
      daily_reports_map.put(current_date, new HashMap<>());
      for (Clinic clinic : class_message.clinics){
        daily_reports_map.get(current_date).
            put(clinic, new DailyAppointmentTypesReport(current_date.atStartOfDay(), clinic));
        for (String service : clinics_general_services){
          ((DailyAppointmentTypesReport)daily_reports_map.get(current_date).get(clinic)).
              report_data.put(service, 0);
        }
      }
      for (ClinicStaff staff_member : clinics_staff) {
        if (!((DailyAppointmentTypesReport) daily_reports_map.get(current_date).get(staff_member.getClinic()))
            .report_data.containsKey(staff_member.getUser().getRole().getName())) {
          ((DailyAppointmentTypesReport)
                  daily_reports_map.get(current_date).get(staff_member.getClinic()))
              .report_data.put(staff_member.getUser().getRole().getName(), 0);
        }
      }
      current_date = current_date.plusDays(1);
    }
  }

  private void initStaffMemberReport() {
    daily_reports_map = new HashMap<>();
    total_appointments_map = new HashMap<>();

    LocalDate current_date = class_message.start_date.toLocalDate();
    LocalDate report_end_date = class_message.end_date.toLocalDate();

    CriteriaQuery<ClinicStaff> cr_ClinicStaff = cb.createQuery(ClinicStaff.class);
    Root<ClinicStaff> root_ClinicStaff = cr_ClinicStaff.from(ClinicStaff.class);
    cr_ClinicStaff.select(root_ClinicStaff).where(root_ClinicStaff.get("clinic").in(class_message.clinics));
    List<ClinicStaff> clinics_staff = session.createQuery(cr_ClinicStaff).getResultList();

    while (!current_date.isAfter(report_end_date)) {
      daily_reports_map.put(current_date, new HashMap<>());
      total_appointments_map.put(current_date, new HashMap<>());
      for (ClinicStaff staff_member : clinics_staff) {
        if (!daily_reports_map.get(current_date).containsKey(staff_member.getClinic())) {
          DailyAverageWaitTimeReport clinic_daily_report =
              new DailyAverageWaitTimeReport(current_date.atStartOfDay(), staff_member.getClinic());
          daily_reports_map.get(current_date).put(staff_member.getClinic(), clinic_daily_report);
          total_appointments_map.get(current_date).put(staff_member.getClinic(), new HashMap<>());
        }
        if (!((DailyAverageWaitTimeReport)
                daily_reports_map.get(current_date).get(staff_member.getClinic()))
            .report_data.containsKey(staff_member.getUser())) {
          ((DailyAverageWaitTimeReport)
                  daily_reports_map.get(current_date).get(staff_member.getClinic()))
              .report_data.put(staff_member.getUser(), 0);
          total_appointments_map
              .get(current_date)
              .get(staff_member.getClinic())
              .put(staff_member.getUser(), 0);
        }
      }
      current_date = current_date.plusDays(1);
    }
  }

  private void getAttendanceReport() {
    cr.select(root)
        .where(
            cb.between(root.get("appt_date"), class_message.start_date, class_message.end_date),
            cb.isTrue(root.get("taken")),
            root.get("clinic").in(class_message.clinics),
            cb.isNotNull(root.get("called_time")));

    relevant_appointments = session.createQuery(cr).getResultList();
    for (Appointment appt : relevant_appointments) {
      LocalDate appt_date = appt.getDate().toLocalDate();
      String service_type;
      if (appt.getStaff_member() == null) {
        service_type = appt.getType().getName();
      } else {
        service_type = appt.getSpecialist_role().getName();
      }
      Clinic appt_clinic = appt.getClinic();

      DailyAppointmentTypesReport report =
          (DailyAppointmentTypesReport) daily_reports_map.get(appt_date).get(appt_clinic);
      report.report_data.put(service_type, report.report_data.get(service_type) + 1);
    }
  }

  private void getAverageWaitTimeReport() {
    cr.select(root)
        .where(
            cb.between(root.get("appt_date"), class_message.start_date, class_message.end_date),
            cb.isTrue(root.get("taken")),
            root.get("clinic").in(class_message.clinics),
            cb.isNotNull(root.get("called_time")));

    relevant_appointments = session.createQuery(cr).getResultList();
    for (Appointment appt : relevant_appointments) {
      LocalDate appt_date = appt.getDate().toLocalDate();
      Clinic appt_clinic = appt.getClinic();
      User appt_staff_member = appt.getStaff_member();
      int wait_time = (int) Duration.between(appt.getDate(), appt.getCalled_time()).toSeconds();

      DailyAverageWaitTimeReport report =
          (DailyAverageWaitTimeReport) daily_reports_map.get(appt_date).get(appt_clinic);

      report.report_data.put(
          appt_staff_member, report.report_data.get(appt_staff_member) + wait_time);

      total_appointments_map
          .get(appt_date)
          .get(appt_clinic)
          .put(
              appt_staff_member,
              total_appointments_map.get(appt_date).get(appt_clinic).get(appt_staff_member) + 1);
    }

    for (LocalDate date : daily_reports_map.keySet()) {
      for (Clinic clinic : daily_reports_map.get(date).keySet()) {
        DailyAverageWaitTimeReport dailies =
            (DailyAverageWaitTimeReport) daily_reports_map.get(date).get(clinic);
        for (User staff_member : dailies.report_data.keySet()) {
          int total_appt = total_appointments_map.get(date).get(clinic).get(staff_member);
          int total_wait_time = dailies.report_data.get(staff_member);
          dailies.report_data.put(staff_member, total_wait_time / total_appt);
        }
      }
    }
  }

  private void getMissedAppointmentsReport() {
    cr.select(root)
        .where(
            cb.between(root.get("appt_date"), class_message.start_date, class_message.end_date),
            cb.isTrue(root.get("taken")),
            root.get("clinic").in(class_message.clinics),
            cb.isNull(root.get("called_time")));

    relevant_appointments = session.createQuery(cr).getResultList();
    for (Appointment appt : relevant_appointments) {
      LocalDate appt_date = appt.getDate().toLocalDate();
      String service_type;
      if (appt.getStaff_member() == null) {
        service_type = appt.getType().getName();
      } else {
        service_type = appt.getSpecialist_role().getName();
      }
      Clinic appt_clinic = appt.getClinic();

      DailyAppointmentTypesReport report =
          (DailyAppointmentTypesReport) daily_reports_map.get(appt_date).get(appt_clinic);
      report.report_data.put(service_type, report.report_data.get(service_type) + 1);
    }
  }
}
