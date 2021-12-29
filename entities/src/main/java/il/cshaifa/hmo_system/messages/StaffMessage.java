package il.cshaifa.hmo_system.messages;

public class StaffMessage extends Message {
  List<ClinicStaff> staff_list;

  public StaffMessage() {
    super(messageType.REQUEST);
  }
}
