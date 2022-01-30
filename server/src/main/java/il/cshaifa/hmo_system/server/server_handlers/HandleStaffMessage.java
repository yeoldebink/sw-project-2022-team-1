package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.ClinicStaffMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.util.HashSet;
import java.util.List;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

import static il.cshaifa.hmo_system.Constants.NAME_COL;
import static il.cshaifa.hmo_system.Constants.PATIENT;
import static il.cshaifa.hmo_system.Constants.ROLE;
import static il.cshaifa.hmo_system.Constants.ROLE_COL;

public class HandleStaffMessage extends MessageHandler {


  private final ClinicStaffMessage class_message;

  public HandleStaffMessage(ClinicStaffMessage message, Session session,
      ConnectionToClient client) {
    super(message, session, client);
    this.class_message = (ClinicStaffMessage) this.message;
  }

  @Override
  public void handleMessage() {
    CriteriaQuery<ClinicStaff> cr = cb.createQuery(ClinicStaff.class);
    Root<ClinicStaff> root = cr.from(ClinicStaff.class);
    cr.select(root);
    class_message.staff_list = session.createQuery(cr).getResultList();

    HashSet<User> assigned_staff = new HashSet<>();
    for (ClinicStaff clinic_staff : class_message.staff_list) {
      assigned_staff.add(clinic_staff.getUser());
    }

    CriteriaQuery<User> user_cr = cb.createQuery(User.class);
    Root<User> user_root = user_cr.from(User.class);
    user_cr
        .select(user_root)
        .where(
            cb.notLike(user_root.get(ROLE_COL).get(NAME_COL), "%Manager%"),
            cb.notEqual(user_root.get(ROLE_COL), ROLE(PATIENT))
        );
    List<User> all_staff = session.createQuery(user_cr).getResultList();

    for (User staff_member : all_staff) {
      if (!assigned_staff.contains(staff_member)) {
        class_message.staff_list.add(new ClinicStaff(new Clinic(), staff_member));
      }
    }

    logSuccess("Pulled staff list");
  }
}
