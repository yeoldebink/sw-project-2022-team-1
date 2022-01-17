package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.AssignedUser;
import java.util.ArrayList;

public class AssignStaffEvent extends Event {
  public ArrayList<AssignedUser> staff;
  public RequestType request;

  public enum RequestType {
    ASSIGN,
    UNASSIGN
  }

  public AssignStaffEvent(ArrayList<AssignedUser> staff, Object sender, RequestType request) {
    super(sender);
    this.staff = staff;
    this.request = request;
  }
}
