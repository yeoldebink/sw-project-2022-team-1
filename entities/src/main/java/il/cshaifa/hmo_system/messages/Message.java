package il.cshaifa.hmo_system.messages;

import java.io.Serializable;

public class Message implements Serializable {

  //  public static final long serialVersionUID = 109115103L;

  public enum messageType {
    REQUEST,
    RESPONSE
  }

  public messageType message_type;
}
