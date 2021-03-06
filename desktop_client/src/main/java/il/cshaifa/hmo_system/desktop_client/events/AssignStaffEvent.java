package il.cshaifa.hmo_system.desktop_client.events;

import il.cshaifa.hmo_system.CommonEnums.StaffAssignmentAction;
import il.cshaifa.hmo_system.client_base.events.Event;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_staff.AssignedUser;
import java.util.ArrayList;

public class AssignStaffEvent extends Event {
  public ArrayList<AssignedUser> staff;
  public StaffAssignmentAction action;

  public AssignStaffEvent(
      ArrayList<AssignedUser> staff, Object sender, StaffAssignmentAction action) {
    super(sender);
    this.staff = staff;
    this.action = action;
  }
}
