package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.time.LocalDateTime;
import java.util.List;

public class OnSiteLoginMessage extends LoginMessage {

  // request
  public Clinic clinic;
  public OnSiteLoginAction action;
  public List<QueuedAppointment> staff_member_queue;
  public LocalDateTime queue_timestamp;

  public OnSiteLoginMessage(int id, String password,
      Clinic clinic, OnSiteLoginAction action) {
    super(MessageType.REQUEST, id, password);
    this.clinic = clinic;
    this.action = action;
  }
}
