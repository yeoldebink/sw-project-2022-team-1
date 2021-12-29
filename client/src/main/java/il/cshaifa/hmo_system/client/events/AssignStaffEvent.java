package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.User;
import java.util.ArrayList;

public class AssignStaffEvent {
  public ArrayList<User> staff;
  public Phase phase;

  public AssignStaffEvent(ArrayList<User> staff, Phase phase) {
    this.staff = staff;
    this.phase = phase;
  }

  public enum Phase {
    ASSIGN,
    UNASSIGN
  }
}
