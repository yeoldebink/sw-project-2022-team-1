package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.messages.Message;
import java.util.List;
import org.hibernate.Session;

public class MessageHandler {
  public Message message;
  public Session session;

  public MessageHandler(Message msg, Session session) {
    this.message = msg;
    this.session = session;
  }

  public void handleMessage(){}

  /**
   * @param entity_list Entities to be added to DB
   */
  protected void saveEntities(List<?> entity_list) {
    for (var entity : entity_list) {
      session.save(entity);
    }
    session.flush();
  }

  /**
   * @param entity_list Entities to be removed from DB
   */
  protected void removeEntities(List<?> entity_list) {
    for (var entity : entity_list) {
      session.remove(entity);
    }
    session.flush();
  }

  /**
   * @param entity_list Entities to be updated to DB
   */
  protected void updateEntities(List<?> entity_list) {
    for (var entity : entity_list) {
      session.update(entity);
    }
    session.flush();
  }
}
