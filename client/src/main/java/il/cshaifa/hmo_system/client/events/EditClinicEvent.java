package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Clinic;

public class EditClinicEvent {
  public Clinic clinic;
  public Phase phase;
  public Object senderInstance;

  public EditClinicEvent(Clinic clinic, Phase phase, Object senderInstance) {
    this.clinic = clinic;
    this.phase = phase;
    this.senderInstance = senderInstance;
  }

  public enum Phase {
    OPEN_WINDOW,
    SEND
  }
}
