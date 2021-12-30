package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.ClinicStaff;
import java.util.List;

public class StaffMessage extends Message {
  public List<ClinicStaff> staff_list;

  public StaffMessage() {
    super(messageType.REQUEST);
  }
}
