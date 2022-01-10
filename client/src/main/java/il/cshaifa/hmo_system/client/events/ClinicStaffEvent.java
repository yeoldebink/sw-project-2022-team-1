package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.ClinicStaff;
import java.util.ArrayList;

public class ClinicStaffEvent extends Event{
  public ArrayList<ClinicStaff> clinic_staff;

  public ClinicStaffEvent(ArrayList<ClinicStaff> clinic_staff, Object senderInstance) {
    super(senderInstance);
    this.clinic_staff = clinic_staff;
  }
}
