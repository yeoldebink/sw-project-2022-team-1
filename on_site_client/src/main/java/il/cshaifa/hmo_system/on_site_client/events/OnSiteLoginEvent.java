package il.cshaifa.hmo_system.on_site_client.events;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.client_base.events.LoginEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.time.LocalDateTime;
import java.util.List;

public class OnSiteLoginEvent extends LoginEvent {
  public Clinic clinic;
  public OnSiteLoginAction action;

  public List<QueuedAppointment> staff_member_queue;
  public LocalDateTime queue_timestamp;

  public OnSiteLoginEvent(int id, String password, Clinic clinic, Object sender) {
    super(id, password, sender);
    this.clinic = clinic;
  }
}
