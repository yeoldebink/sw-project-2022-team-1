package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.CommonEnums.GreenPassStatus;
import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.messages.GreenPassStatusMessage;
import il.cshaifa.hmo_system.messages.Message;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

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
            cb.equal(root.get("patient"), patient),
            cb.equal(root.get("type").get("name"), "COVID Test"),
            cb.lessThanOrEqualTo(root.get("appt_date"), LocalDateTime.now()),
            cb.isNotNull(root.get("called_time")));
    cr.orderBy(cb.asc(root.get("appt_date")));
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
            cb.equal(root.get("patient"), patient),
            cb.equal(root.get("type").get("name"), "COVID Vaccine"),
            cb.lessThanOrEqualTo(root.get("appt_date"), LocalDateTime.now()),
            cb.isNotNull(root.get("called_time")));
    cr.orderBy(cb.asc(root.get("appt_date")));
    List<Appointment> covid_vaccine_appt = session.createQuery(cr).getResultList();
    if (covid_vaccine_appt.size() > 0) {
      class_message.last_vaccine = covid_vaccine_appt.get(0).getDate();
      return covid_vaccine_appt.get(0).getDate();
    }
    return null;
  }
}
