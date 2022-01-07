package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.AssignedUser;
import java.util.ArrayList;

public class AssignStaffEvent {
  public ArrayList<AssignedUser> staff;
  public Object senderInstance;
  public StaffStatus status;

  public enum StaffStatus{
    ASSIGN,
    UNASSIGN
  }

  public AssignStaffEvent(ArrayList<AssignedUser> staff, Object senderInstance, StaffStatus status) {
    this.staff = staff;
    this.senderInstance = senderInstance;
    this.status = status;
  }
}
