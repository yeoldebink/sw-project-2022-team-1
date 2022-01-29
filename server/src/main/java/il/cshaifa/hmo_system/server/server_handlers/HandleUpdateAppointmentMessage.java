package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.messages.Message;
import il.cshaifa.hmo_system.messages.UpdateAppointmentMessage;
import org.hibernate.Session;

public class HandleUpdateAppointmentMessage extends MessageHandler {
  public UpdateAppointmentMessage class_message;

  public HandleUpdateAppointmentMessage(Message msg,
      Session session) {
    super(msg, session);
    this.class_message = (UpdateAppointmentMessage) message;
  }

  @Override
  public void handleMessage() {
    session.update(class_message.appointment);
    session.flush();
  }
}
