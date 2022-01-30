package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.messages.Message;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.Session;

public abstract class MessageHandler {
  public Message message;
  public Session session;
  public ConnectionToClient client;
  protected static CriteriaBuilder cb;

  private final static Logger LOGGER = Logger.getLogger(MessageHandler.class.getSimpleName());

  public MessageHandler(Message msg, Session session, ConnectionToClient client) {
    this.client = client;
    this.message = msg;
    this.session = session;
    if (cb == null) {
      cb = session.getCriteriaBuilder();
    }
  }

  public abstract void handleMessage();

  protected void logInfo(String msg) {
    LOGGER.info(String.format("%s %s %s : %s", getClass().getSimpleName(), client.getInfo("user_str"), client.getInetAddress(), msg));
  }

  protected void logFailure(String msg) {
    logInfo(String.format(" * FAILURE [%s]", msg));
  }

  protected void logSuccess(String msg) {
    logInfo(String.format("SUCCESS [%s]", msg));
  }

  /** @param entity_list Entities to be added to DB */
  protected void saveEntities(List<?> entity_list) {
    for (var entity : entity_list) {
      session.save(entity);
    }
    session.flush();
  }

  /** @param entity_list Entities to be removed from DB */
  protected void removeEntities(List<?> entity_list) {
    for (var entity : entity_list) {
      session.remove(entity);
    }
    session.flush();
  }

  /** @param entity_list Entities to be updated to DB */
  protected void updateEntities(List<?> entity_list) {
    for (var entity : entity_list) {
      session.update(entity);
    }
    session.flush();
  }
}
