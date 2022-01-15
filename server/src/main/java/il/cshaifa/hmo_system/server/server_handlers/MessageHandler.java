package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.messages.Message;
import org.hibernate.Session;

public class MessageHandler {
  public Message message;
  public Session session;

  public MessageHandler(Message msg, Session session) {
    this.message = msg;
    this.session = session;
  }

  public void handleMessage(){}
}
