package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Clinic;
import java.util.ArrayList;

public class ClinicEvent extends Event{

  public ArrayList<Clinic> receivedClinics;
  public Clinic clinic;

  public ClinicEvent(ArrayList<Clinic> clinics, Object senderInstance) {
    super(senderInstance);
    this.receivedClinics = clinics;
    this.clinic = null;
  }

  public ClinicEvent(Clinic clinic, Object senderInstance) {
    super(senderInstance);
    this.clinic = clinic;
    this.receivedClinics = null;
  }
}
