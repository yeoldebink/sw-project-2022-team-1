package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.CommonEnums.GreenPassStatus;
import il.cshaifa.hmo_system.entities.Patient;
import java.time.LocalDateTime;

public class GreenPassStatusMessage extends Message{

  public LocalDateTime last_vaccine;
  public LocalDateTime last_covid_test;
  public GreenPassStatus status;
  public Patient patient;

  public GreenPassStatusMessage(Patient patient) {
    super(MessageType.REQUEST);
    this.patient = patient;
  }
}
