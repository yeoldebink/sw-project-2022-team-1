package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.ClinicStaff;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.messages.ClinicStaffMessage;
import java.util.HashSet;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;

public class handleStaffMessage extends MessageHandler {
  private ClinicStaffMessage class_message;

  public handleStaffMessage(ClinicStaffMessage message, Session session) {
    super(message, session);
    this.class_message = (ClinicStaffMessage) this.message;
  }

  @Override
  public void handleMessage() {
    CriteriaBuilder cb = session.getCriteriaBuilder();
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
    user_cr.select(user_root).where(
        cb.notLike(user_root.get("role").get("name"), "%Manager%"),
        cb.notEqual(user_root.get("role").get("name"), "Patient"));
    List<User> all_staff = session.createQuery(user_cr).getResultList();

    for (User staff_member : all_staff) {
      if (!assigned_staff.contains(staff_member)) {
        class_message.staff_list.add(new ClinicStaff(new Clinic(), staff_member));
      }
    }
  }
}
