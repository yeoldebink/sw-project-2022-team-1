package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Clinic;

public class OnSiteLoginMessage extends LoginMessage {
  // request
  public Clinic clinic;

  // response
  public boolean authorized;

  public OnSiteLoginMessage(MessageType message_type, int id, String password,
      Clinic clinic, boolean authorized) {
    super(message_type, id, password);
    this.clinic = clinic;
    this.authorized = authorized;
  }
}
