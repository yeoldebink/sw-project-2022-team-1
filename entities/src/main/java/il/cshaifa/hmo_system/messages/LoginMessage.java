package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.User;

public class LoginMessage extends Message {
  /* Request fields */
  public int id;
  public String password;
  /* Respond fields */
  public User user;

  public LoginMessage(MessageType message_type, int id, String password) {
    super(message_type);
    this.id = id;
    this.password = password;
    this.user = null;
  }
}
