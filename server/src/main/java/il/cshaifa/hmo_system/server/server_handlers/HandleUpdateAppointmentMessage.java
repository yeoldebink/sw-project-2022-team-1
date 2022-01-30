package il.cshaifa.hmo_system.server.server_handlers;

import il.cshaifa.hmo_system.messages.Message;
import il.cshaifa.hmo_system.messages.UpdateAppointmentMessage;
import il.cshaifa.hmo_system.server.ocsf.ConnectionToClient;
import org.hibernate.Session;

public class HandleUpdateAppointmentMessage extends MessageHandler {

  public UpdateAppointmentMessage class_message;

  public HandleUpdateAppointmentMessage(Message msg, Session session, ConnectionToClient client) {
    super(msg, session, client);
    this.class_message = (UpdateAppointmentMessage) message;
  }

  @Override
  public void handleMessage() {
    session.update(class_message.appointment);
    session.flush();

    logSuccess("Updated appointment details");
  }
}
