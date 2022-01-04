package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_staff.AssignedUser;
import java.util.ArrayList;

public class AssignStaffEvent {
  public ArrayList<AssignedUser> staff;
  public Phase phase;

  public AssignStaffEvent(ArrayList<AssignedUser> staff, Phase phase) {
    this.staff = staff;
    this.phase = phase;
  }

  public enum Phase {
    ASSIGN,
    UNASSIGN,
    RESPOND
  }
}
