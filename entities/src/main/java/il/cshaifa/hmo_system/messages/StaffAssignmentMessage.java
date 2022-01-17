package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.User;
import java.util.List;

public class StaffAssignmentMessage extends Message {
  public enum RequestType {
    ASSIGN,
    UNASSIGN
  }

  public List<User> staff;
  public Clinic clinic;
  public RequestType request;

  public StaffAssignmentMessage(List<User> staff, Clinic clinic, RequestType request) {
    super(MessageType.REQUEST);
    this.staff = staff;
    this.clinic = clinic;
    this.request = request;
  }
}
