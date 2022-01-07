package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Clinic;
import java.util.ArrayList;

public class ClinicEvent {

  public ArrayList<Clinic> receivedClinics;
  public Clinic clinic;
  public Object senderInstance;

  public ClinicEvent(ArrayList<Clinic> clinics, Object senderInstance) {
    this.receivedClinics = clinics;
    this.clinic = null;
    this.senderInstance = senderInstance;
  }

  public ClinicEvent(Clinic clinic, Object senderInstance) {
    this.clinic = clinic;
    this.receivedClinics = null;
    this.senderInstance = senderInstance;
  }
}
