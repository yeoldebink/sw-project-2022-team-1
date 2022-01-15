package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.messages.ClinicMessage;
import org.hibernate.Session;

public class handleClinicMessage extends MessageHandler {

  public handleClinicMessage(Session session, ClinicMessage message) {
    super(session, message);
  }

  public void handleMessage(){}
}
