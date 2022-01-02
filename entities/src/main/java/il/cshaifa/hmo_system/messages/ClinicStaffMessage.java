package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.ClinicStaff;
import java.util.List;

public class ClinicStaffMessage extends Message {
  public List<ClinicStaff> staff_list;

  public ClinicStaffMessage() {
    super(messageType.REQUEST);
  }
}
