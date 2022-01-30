package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_staff;

import il.cshaifa.hmo_system.entities.User;

/**
 * Extends the User entity with a boolean field to indicate whether this user is assigned to this
 * clinic or not
 */
public class AssignedUser extends User {
  private final Boolean assigned;

  public AssignedUser(User user, Boolean assigned) {
    super(user);
    this.assigned = assigned;
  }

  public Boolean getAssigned() {
    return assigned;
  }

  public String getRoleName() {
    return this.getRole().getName();
  }
}
