package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.messages.Message;

public class ResponseEvent {
  public Message response;

  public ResponseEvent(Message response) {
    this.response = response;
  }
}
