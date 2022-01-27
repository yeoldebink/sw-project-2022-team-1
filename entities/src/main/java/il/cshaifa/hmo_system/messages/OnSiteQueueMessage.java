package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.util.List;

public class OnSiteQueueMessage extends Message {

  public enum Action {
    PUSH,
    POP
  }

  // shared for all messages of this type
  public Action action;

  // for pushing
  public Patient patient;
  public AppointmentType appt_type;

  // response
  public QueuedAppointment q_appt;
  public List<QueuedAppointment> updated_queue;

  private OnSiteQueueMessage(MessageType message_type) {
    super(message_type);
    this.action = Action.POP;
  }

  private OnSiteQueueMessage(
      MessageType message_type,Patient patient, AppointmentType appt_type) {
    super(message_type);
    this.patient = patient;
    this.appt_type = appt_type;
    this.action = Action.PUSH;
  }

  /**
   * Creates a message sent by a staff member to pop their next appointment
   *
   * @return
   */
  public static OnSiteQueueMessage popMessage() {
    return new OnSiteQueueMessage(MessageType.REQUEST);
  }

  /**
   * Creates a message sent by a patient to join the queue for the lab or nurse's station
   *
   * @param patient
   * @param appt_type
   * @return
   */
  public static OnSiteQueueMessage pushMessage(
      Patient patient, AppointmentType appt_type) {
    return new OnSiteQueueMessage(MessageType.REQUEST, patient, appt_type);
  }
}
