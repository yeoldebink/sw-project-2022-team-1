package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.ClinicStaff;

public class StaffMessage extends Message{
  List<ClinicStaff> staff_list;

  public StaffMessage() {
    super(messageType.REQUEST);
  }
}
