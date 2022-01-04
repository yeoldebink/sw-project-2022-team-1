package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.util.List;

public class StaffAssignmentMessage extends Message {
  public enum Type {
    ASSIGN,
    UNASSIGN
  }

  public List<User> staff;
  public Clinic clinic;
  public Type type;

  public StaffAssignmentMessage(List<User> staff, Clinic clinic, Type type) {
    super(MessageType.REQUEST);
    this.staff = staff;
    this.clinic = clinic;
    this.type = type;
  }
}
