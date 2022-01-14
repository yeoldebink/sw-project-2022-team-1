package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.AssignedUser;
import java.util.ArrayList;

public class AssignStaffEvent extends Event {
  public ArrayList<AssignedUser> staff;
  public Action status;

  public enum Action {
    ASSIGN,
    UNASSIGN
  }

  public AssignStaffEvent(ArrayList<AssignedUser> staff, Object sender, Action status) {
    super(sender);
    this.staff = staff;
    this.status = status;
  }
}
