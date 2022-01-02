package il.cshaifa.hmo_system.messages;

import java.io.Serializable;

public class Message implements Serializable {

  public enum MessageType {
    REQUEST,
    RESPONSE
  }

  public MessageType message_type;

  public Message(MessageType message_type) {
    this.message_type = message_type;
  }
}
