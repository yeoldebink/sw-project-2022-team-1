package il.cshaifa.hmo_system.server.server_handlers;

import static il.cshaifa.hmo_system.Constants.APPT_DATE_COL;
import static il.cshaifa.hmo_system.Constants.CALLED_TIME_COL;
import static il.cshaifa.hmo_system.Constants.CLINIC_COL;
import static il.cshaifa.hmo_system.Constants.STAFF_MEMBER_COL;
import static il.cshaifa.hmo_system.Constants.TAKEN_COL;
import static il.cshaifa.hmo_system.Constants.UNSTAFFED_APPT_TYPES;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.messages.ReportMessage;
import il.cshaifa.hmo_system.reports.DailyAppointmentTypesReport;
import il.cshaifa.hmo_system.reports.DailyAverageWaitTimeReport;
import il.cshaifa.hmo_system.reports.DailyReport;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleReportMessage extends MessageHandler {
  ReportMessage class_message;
  //  LocalDate -> ClinicID -> DailyReport
  private HashMap<LocalDate, HashMap<Integer, DailyReport>> daily_reports_map;
  // LocalDate -> ClinicID -> total user appointments
  HashMap<LocalDate, HashMap<Integer, Integer>> total_appointments_map;
  private final CriteriaQuery<Appointment> cr;
  private final Root<Appointment> root;
  private List<Appointment> relevant_appointments;

  public HandleReportMessage(ReportMessage message, Session session, ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (ReportMessage) this.message;
    cr = cb.createQuery(Appointment.class);
    root = cr.from(Appointment.class);
  }

  @Override
  public void handleMessage() {
    switch (class_message.report_type) {
      case APPOINTMENT_ATTENDANCE:
        initServicesReport();
        getAttendanceReport();
        break;

      case AVERAGE_WAIT_TIMES:
        initStaffMemberReport();
        getAverageWaitTimeReport();
        break;

      case MISSED_APPOINTMENTS:
        initServicesReport();
        getMissedAppointmentsReport();
        break;
    }

    class_message.reports = new ArrayList<>();
    for (LocalDate date : daily_reports_map.keySet()) {
      for (int clinic_id : daily_reports_map.get(date).keySet()) {
        class_message.reports.add(daily_reports_map.get(date).get(clinic_id));
      }
    }

    logSuccess(class_message.report_type.toString());
  }

  private void initServicesReport() {
    LocalDate report_end_date = class_message.end_date.toLocalDate();
    daily_reports_map = new HashMap<>();

    List<ClinicStaff> clinics_staff = getClinicStaff(class_message.clinics);

    for (LocalDate current_date = class_message.start_date.toLocalDate();
        !current_date.isAfter(report_end_date);
        current_date = current_date.plusDays(1)) {
      daily_reports_map.put(current_date, new HashMap<>());
      // for each clinic fill General services as 0
      for (Clinic clinic : class_message.clinics) {
        daily_reports_map
            .get(current_date)
            .put(
                clinic.getId(),
                new DailyAppointmentTypesReport(current_date.atStartOfDay(), clinic));
        for (var service : UNSTAFFED_APPT_TYPES) {
          ((DailyAppointmentTypesReport) daily_reports_map.get(current_date).get(clinic.getId()))
              .report_data.put(service.getName(), 0);
        }
      }
      // for each staff member fill his report at certain clinic make 0-report
      for (ClinicStaff staff_member : clinics_staff) {
        ((DailyAppointmentTypesReport)
                daily_reports_map.get(current_date).get(staff_member.getClinic().getId()))
            .report_data.putIfAbsent(staff_member.getUser().getRole().getName(), 0);
      }
    }
  }

  private void initStaffMemberReport() {
    daily_reports_map = new HashMap<>();
    total_appointments_map = new HashMap<>();

    LocalDate report_end_date = class_message.end_date.toLocalDate();

    for (LocalDate current_date = class_message.start_date.toLocalDate();
        !current_date.isAfter(report_end_date);
        current_date = current_date.plusDays(1)) {
      daily_reports_map.put(current_date, new HashMap<>());
      total_appointments_map.put(current_date, new HashMap<>());
      for (Clinic clinic : class_message.clinics) {
        DailyAverageWaitTimeReport clinic_daily_report =
            new DailyAverageWaitTimeReport(current_date.atStartOfDay(), clinic);

        daily_reports_map.get(current_date).putIfAbsent(clinic.getId(), clinic_daily_report);

        total_appointments_map.get(current_date).put(clinic.getId(), 0);

        ((DailyAverageWaitTimeReport) daily_reports_map.get(current_date).get(clinic.getId()))
            .report_data.put(class_message.staff_member.getUser(), 0);

        total_appointments_map.get(current_date).put(clinic.getId(), 0);
      }
    }
  }

  private void getAttendanceReport() {
    cr.select(root)
        .where(
            cb.between(root.get(APPT_DATE_COL), class_message.start_date, class_message.end_date),
            cb.isTrue(root.get(TAKEN_COL)),
            root.get(CLINIC_COL).in(class_message.clinics),
            cb.isNotNull(root.get(CALLED_TIME_COL)));

    relevant_appointments = session.createQuery(cr).getResultList();
    for (Appointment appt : relevant_appointments) {
      LocalDate appt_date = appt.getDate().toLocalDate();
      String service_type;
      if (appt.getStaff_member() == null) {
        service_type = appt.getType().getName();
      } else {
        service_type = appt.getStaff_member().getRole().getName();
      }
      Clinic appt_clinic = appt.getClinic();

      DailyAppointmentTypesReport report =
          (DailyAppointmentTypesReport) daily_reports_map.get(appt_date).get(appt_clinic.getId());
      report.report_data.put(service_type, report.report_data.get(service_type) + 1);
    }
  }

  private void getAverageWaitTimeReport() {
    cr.select(root)
        .where(
            cb.equal(root.get(STAFF_MEMBER_COL), class_message.staff_member.getUser()),
            cb.between(root.get(APPT_DATE_COL), class_message.start_date, class_message.end_date),
            cb.isTrue(root.get(TAKEN_COL)),
            root.get(CLINIC_COL).in(class_message.clinics),
            cb.isNotNull(root.get(CALLED_TIME_COL)));

    relevant_appointments = session.createQuery(cr).getResultList();
    for (Appointment appt : relevant_appointments) {
      LocalDate appt_date = appt.getDate().toLocalDate();
      Clinic appt_clinic = appt.getClinic();
      var appt_staff_member = appt.getStaff_member();
      int wait_time = (int) Duration.between(appt.getDate(), appt.getCalled_time()).toSeconds();

      DailyAverageWaitTimeReport report =
          (DailyAverageWaitTimeReport) daily_reports_map.get(appt_date).get(appt_clinic.getId());
      var total_waited_time = report.report_data.get(appt_staff_member);

      if (total_waited_time == null) {
        report.report_data.put(appt_staff_member, wait_time);
      } else {
        report.report_data.put(
            appt_staff_member, report.report_data.get(appt_staff_member) + wait_time);
      }
      total_appointments_map
          .get(appt_date)
          .put(
              appt_clinic.getId(),
              total_appointments_map.get(appt_date).get(appt_clinic.getId()) + 1);
    }

    for (LocalDate date : daily_reports_map.keySet()) {
      for (int clinic_id : daily_reports_map.get(date).keySet()) {
        DailyAverageWaitTimeReport dailies =
            (DailyAverageWaitTimeReport) daily_reports_map.get(date).get(clinic_id);
        for (var staff_member : dailies.report_data.keySet()) {
          int total_appt = total_appointments_map.get(date).get(clinic_id);
          int total_wait_time = dailies.report_data.get(staff_member);
          if (total_appt == 0) {
            dailies.report_data.put(staff_member, null);
          } else {
            dailies.report_data.put(staff_member, total_wait_time / total_appt);
          }
        }
      }
    }
  }

  private void getMissedAppointmentsReport() {
    cr.select(root)
        .where(
            cb.between(root.get(APPT_DATE_COL), class_message.start_date, class_message.end_date),
            cb.isTrue(root.get(TAKEN_COL)),
            root.get(CLINIC_COL).in(class_message.clinics),
            cb.isNull(root.get(CALLED_TIME_COL)));

    relevant_appointments = session.createQuery(cr).getResultList();
    for (Appointment appt : relevant_appointments) {
      LocalDate appt_date = appt.getDate().toLocalDate();
      String service_type;
      if (appt.getStaff_member() == null) {
        service_type = appt.getType().getName();
      } else {
        service_type = appt.getStaff_member().getRole().getName();
      }
      Clinic appt_clinic = appt.getClinic();

      DailyAppointmentTypesReport report =
          (DailyAppointmentTypesReport) daily_reports_map.get(appt_date).get(appt_clinic.getId());
      report.report_data.put(service_type, report.report_data.get(service_type) + 1);
    }
  }

  private List<ClinicStaff> getClinicStaff(List<Clinic> clinics) {
    CriteriaQuery<ClinicStaff> cr_ClinicStaff = cb.createQuery(ClinicStaff.class);
    Root<ClinicStaff> root_ClinicStaff = cr_ClinicStaff.from(ClinicStaff.class);
    cr_ClinicStaff.select(root_ClinicStaff).where(root_ClinicStaff.get(CLINIC_COL).in(clinics));
    return session.createQuery(cr_ClinicStaff).getResultList();
  }
}
