package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleClinicMessage extends MessageHandler {
  ClinicMessage class_message;

  public HandleClinicMessage(ClinicMessage message, Session session,
      ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (ClinicMessage) this.message;
  }

  /**
   * If message.clinics is null, client requested all the clinics else, client has made changes to
   * clinics, so apply changes to DB
   */
  @Override
  public void handleMessage() {
    if (class_message.clinics == null) {
      getClinics();
    } else {
      updateClinics();
    }
  }

  /** Get clinics list */
  protected void getClinics() {
    CriteriaQuery<Clinic> cr = cb.createQuery(Clinic.class);
    Root<Clinic> root = cr.from(Clinic.class);
    cr.select(root);
    class_message.clinics = session.createQuery(cr).getResultList();
  }

  /** Update changed clinics to DB */
  private void updateClinics() {
    updateEntities(class_message.clinics);
  }
}
