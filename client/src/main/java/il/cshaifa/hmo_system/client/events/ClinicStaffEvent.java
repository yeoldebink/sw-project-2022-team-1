package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.ClinicStaff;
import java.util.ArrayList;

public class ClinicStaffEvent {
  public ArrayList<ClinicStaff> clinic_staff;
  public Object senderInstance;

  public ClinicStaffEvent(ArrayList<ClinicStaff> clinic_staff, Object senderInstance) {
    this.clinic_staff = clinic_staff;
    this.senderInstance = senderInstance;
  }
}
