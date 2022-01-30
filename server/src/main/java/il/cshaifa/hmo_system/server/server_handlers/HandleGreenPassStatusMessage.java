package il.cshaifa.hmo_system.server.server_handlers;

import com.mysql.cj.log.Log;
import il.cshaifa.hmo_system.CommonEnums.GreenPassStatus;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.messages.GreenPassStatusMessage;
import il.cshaifa.hmo_system.messages.Message;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

import static il.cshaifa.hmo_system.Constants.APPT_DATE_COL;
import static il.cshaifa.hmo_system.Constants.APPT_TYPE;
import static il.cshaifa.hmo_system.Constants.CALLED_TIME_COL;
import static il.cshaifa.hmo_system.Constants.COVID_TEST;
import static il.cshaifa.hmo_system.Constants.COVID_VACCINE;
import static il.cshaifa.hmo_system.Constants.NAME_COL;
import static il.cshaifa.hmo_system.Constants.PATIENT_COL;
import static il.cshaifa.hmo_system.Constants.TYPE_COL;

public class HandleGreenPassStatusMessage extends MessageHandler {

  private final GreenPassStatusMessage class_message;
  private final Patient patient;
  private final CriteriaQuery<Appointment> cr;
  private final Root<Appointment> root;


  public HandleGreenPassStatusMessage(Message message, Session session,
      ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (GreenPassStatusMessage) this.message;
    this.patient = ((GreenPassStatusMessage) message).patient;
    cr = cb.createQuery(Appointment.class);
    root = cr.from(Appointment.class);
  }

  @Override
  public void handleMessage() {
    class_message.status = getGreenPassStatus();
    logInfo(class_message.status.toString());
  }

  /** Updates the status of patients COVID-19 green-pass */
  public GreenPassStatus getGreenPassStatus() {
    LocalDateTime last_vaccine = getLastCovidVaccineDate(), last_test = getLastCovidTestDate();

    if (last_vaccine == null || last_vaccine.plusMonths(6).isBefore(LocalDateTime.now())) {
      if (last_test == null || last_test.plusDays(3).isBefore(LocalDateTime.now())) {
        return GreenPassStatus.REJECT;
      }
      return GreenPassStatus.TESTED;
    }
    return GreenPassStatus.VACCINATED;
  }

  /** @return The date of the patients last COVID-19 test. */
  public LocalDateTime getLastCovidTestDate() {
    cr.select(root)
        .where(
            cb.equal(root.get(PATIENT_COL), patient),
            cb.equal(root.get(TYPE_COL), APPT_TYPE(COVID_TEST)),
            cb.lessThanOrEqualTo(root.get(APPT_DATE_COL), LocalDateTime.now()),
            cb.isNotNull(root.get(CALLED_TIME_COL)));
    cr.orderBy(cb.asc(root.get(APPT_DATE_COL)));
    List<Appointment> covid_test_appt = session.createQuery(cr).getResultList();
    if (covid_test_appt.size() > 0) {
      class_message.last_covid_test = covid_test_appt.get(0).getDate();
      return covid_test_appt.get(0).getDate();
    }
    return null;
  }

  /** @return The date of the patients last COVID-19 vaccine. */
  public LocalDateTime getLastCovidVaccineDate() {
    cr.select(root)
        .where(
            cb.equal(root.get(PATIENT_COL), patient),
            cb.equal(root.get(TYPE_COL), APPT_TYPE(COVID_VACCINE)),
            cb.lessThanOrEqualTo(root.get(APPT_DATE_COL), LocalDateTime.now()),
            cb.isNotNull(root.get(CALLED_TIME_COL)));
    cr.orderBy(cb.asc(root.get(APPT_DATE_COL)));
    List<Appointment> covid_vaccine_appt = session.createQuery(cr).getResultList();
    if (covid_vaccine_appt.size() > 0) {
      class_message.last_vaccine = covid_vaccine_appt.get(0).getDate();
      return covid_vaccine_appt.get(0).getDate();
    }
    return null;
  }
}
