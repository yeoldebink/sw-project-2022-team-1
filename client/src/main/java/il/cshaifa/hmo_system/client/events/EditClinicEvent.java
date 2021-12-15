package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Clinic;

public class EditClinicEvent {
  public Clinic clinic;
  public Phase phase;
  public EditClinicEvent(Clinic clinic, Phase phase) {
    this.clinic = clinic;
    this.phase = phase;
  }

  public enum Phase {
    OPEN_WINDOW,
    SEND
  }
}
