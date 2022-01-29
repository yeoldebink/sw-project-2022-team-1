package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.CommonEnums.OnSiteQueueRejectionReason;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.time.LocalDateTime;
import java.util.List;

public class OnSiteQueueMessage extends Message {

  public enum Action {
    PUSH,
    POP,
    UPDATE_QUEUE
  }

  // shared for all messages of this type
  public Action action;

  // for pushing
  public Patient patient;
  public AppointmentType appt_type;

  // response
  public QueuedAppointment q_appt;
  public List<QueuedAppointment> updated_queue;
  public LocalDateTime queue_timestamp;
  public OnSiteQueueRejectionReason rejection_reason;

  private OnSiteQueueMessage(MessageType message_type) {
    super(message_type);
    this.action = Action.POP;
    this.rejection_reason = null;
  }

  private OnSiteQueueMessage(
      MessageType message_type,Patient patient, AppointmentType appt_type) {
    super(message_type);
    this.patient = patient;
    this.appt_type = appt_type;
    this.action = Action.PUSH;
    this.rejection_reason = null;
  }

  private OnSiteQueueMessage(MessageType messageType, List<QueuedAppointment> updated_queue, LocalDateTime queue_timestamp) {
    super(messageType);
    this.updated_queue = updated_queue;
    this.queue_timestamp = queue_timestamp;
    this.rejection_reason = null;
    this.action = Action.UPDATE_QUEUE;
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

  /**
   * Creates an update message for the clients
   */
  public static OnSiteQueueMessage updateMessage(List<QueuedAppointment> updated_queue, LocalDateTime queue_timestamp) {
    return new OnSiteQueueMessage(MessageType.RESPONSE, updated_queue, queue_timestamp);
  }
}
