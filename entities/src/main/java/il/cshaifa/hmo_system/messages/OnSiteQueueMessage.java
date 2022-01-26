package il.cshaifa.hmo_system.messages;

import il.cshaifa.hmo_system.entities.Appointment;
import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;

public class OnSiteQueueMessage extends Message {

  public enum Action {
    PUSH,
    POP
  }

  public Action action;
  public Clinic clinic;

  public Patient patient;
  public AppointmentType appt_type;

  public User staff_member;

  public String number_in_line;
  public Appointment appointment;

  private OnSiteQueueMessage(MessageType message_type, Clinic clinic, User staff_member) {
    super(message_type);
    this.clinic = clinic;
    this.staff_member = staff_member;
    this.action = Action.POP;
  }

  private OnSiteQueueMessage(
      MessageType message_type, Clinic clinic, Patient patient, AppointmentType appt_type) {
    super(message_type);
    this.clinic = clinic;
    this.patient = patient;
    this.appt_type = appt_type;
    this.action = Action.PUSH;
  }

  /**
   * Creates a message sent by a staff member to pop their next appointment
   *
   * @param clinic
   * @param staff_member
   * @return
   */
  public static OnSiteQueueMessage popMessage(Clinic clinic, User staff_member) {
    return new OnSiteQueueMessage(MessageType.REQUEST, clinic, staff_member);
  }

  /**
   * Creates a message sent by a patient to join the queue for the lab or nurse's station
   *
   * @param clinic
   * @param patient
   * @param appt_type
   * @return
   */
  public static OnSiteQueueMessage pushMessage(
      Clinic clinic, Patient patient, AppointmentType appt_type) {
    return new OnSiteQueueMessage(MessageType.REQUEST, clinic, patient, appt_type);
  }
}
