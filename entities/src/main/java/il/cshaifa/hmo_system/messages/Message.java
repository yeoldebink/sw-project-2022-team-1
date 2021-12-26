package il.cshaifa.hmo_system.messages;

import java.io.Serializable;

public class Message implements Serializable {

  public enum messageType {
    REQUEST,
    RESPONSE
  }

  public messageType message_type;

  public Message(messageType message_type) {
    this.message_type = message_type;
  }
}
