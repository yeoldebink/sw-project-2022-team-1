package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Clinic;
import java.util.ArrayList;

public class ClinicEvent {

  public ArrayList<Clinic> receivedClinics;
  public Clinic clinic;
  public Phase phase;

  public ClinicEvent() {
    this.phase = Phase.REQUEST;
  }

  public ClinicEvent(ArrayList<Clinic> clinics) {
    this.receivedClinics = clinics;
    this.clinic = null;
    this.phase = Phase.LIST;
  }

  public ClinicEvent(Clinic clinic, Phase phase) {
    this.clinic = clinic;
    this.receivedClinics = null;
    this.phase = phase;
  }

  public enum Phase {
    EDIT,
    REQUEST,
    LIST
  }
}
