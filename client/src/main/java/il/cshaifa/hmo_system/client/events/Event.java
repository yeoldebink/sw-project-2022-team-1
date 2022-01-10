package il.cshaifa.hmo_system.client.events;

public class Event {
  private final Object sender;

  public Event(Object sender) {
    this.sender = sender;
  }

  public Object getSender() {
    return sender;
  }
}
