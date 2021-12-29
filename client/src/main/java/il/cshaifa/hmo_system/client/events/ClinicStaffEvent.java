package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.ClinicStaff;
import java.util.ArrayList;

public class ClinicStaffEvent {
  public ArrayList<ClinicStaff> clinic_staff;
  public Phase phase;

  public ClinicStaffEvent(ArrayList<ClinicStaff> clinic_staff, Phase phase) {
    this.clinic_staff = clinic_staff;
    this.phase = phase;
  }

  public enum Phase {
    REQUEST,
    RECEIVE
  }
}
