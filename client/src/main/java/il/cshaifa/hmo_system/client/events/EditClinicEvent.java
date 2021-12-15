package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Clinic;

public class EditClinicEvent {
  public Clinic clinic;

  public EditClinicEvent(Clinic clinic) {
    this.clinic = clinic;
  }
}
