package il.cshaifa.hmo_system.desktop_client.events;

import il.cshaifa.hmo_system.CommonEnums.GreenPassStatus;
import il.cshaifa.hmo_system.client_base.events.Event;
import java.time.LocalDateTime;

public class GreenPassStatusEvent extends Event {

  public LocalDateTime last_vaccine;
  public LocalDateTime last_covid_test;
  public GreenPassStatus status;

  public GreenPassStatusEvent(
      Object sender,
      LocalDateTime last_vaccine,
      LocalDateTime last_covid_test,
      GreenPassStatus status) {
    super(sender);
    this.last_vaccine = last_vaccine;
    this.last_covid_test = last_covid_test;
    this.status = status;
  }

  public GreenPassStatusEvent(Object sender) {
    super(sender);
  }
}
