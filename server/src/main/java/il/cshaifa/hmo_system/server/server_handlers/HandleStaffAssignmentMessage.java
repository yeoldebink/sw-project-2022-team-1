package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.CommonEnums.StaffAssignmentAction;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.StaffAssignmentMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class HandleStaffAssignmentMessage extends MessageHandler {

  private final StaffAssignmentMessage class_message;

  public HandleStaffAssignmentMessage(StaffAssignmentMessage message, Session session,
      ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (StaffAssignmentMessage) this.message;
  }

  @Override
  public void handleMessage() {
    switch (class_message.action) {
      case ASSIGN:
        assignStaff();
        break;

      case UNASSIGN:
        unassignStaff();
        break;
    }

    logSuccess(class_message.action.toString());
  }

  private void assignStaff() {
    for (User staff_member : class_message.staff) {
      session.merge(new ClinicStaff(class_message.clinic, staff_member));
    }
    session.flush();
  }

  private void unassignStaff() {
    CriteriaQuery<ClinicStaff> cr = cb.createQuery(ClinicStaff.class);
    Root<ClinicStaff> root = cr.from(ClinicStaff.class);

    for (User staff_member : class_message.staff) {
      ClinicStaff assignment = new ClinicStaff(class_message.clinic, staff_member);
      cr.select(root)
          .where(
              cb.equal(root.get("user"), assignment.getUser()),
              cb.equal(root.get("clinic"), assignment.getClinic()));
      List<ClinicStaff> staff_to_remove = session.createQuery(cr).getResultList();
      if (staff_to_remove.size() > 0) {
        session.delete(staff_to_remove.get(0));
      }
    }
    session.flush();
  }
}
