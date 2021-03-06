package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.CommonEnums.StaffAssignmentAction;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.util.List;

public class StaffAssignmentMessage extends Message {

  public List<User> staff;
  public Clinic clinic;
  public StaffAssignmentAction action;

  public StaffAssignmentMessage(List<User> staff, Clinic clinic, StaffAssignmentAction action) {
    super(MessageType.REQUEST);
    this.staff = staff;
    this.clinic = clinic;
    this.action = action;
  }
}
