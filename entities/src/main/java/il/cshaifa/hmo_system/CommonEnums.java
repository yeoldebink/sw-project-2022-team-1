package il.cshaifa.hmo_system;

public class CommonEnums {
  public enum SetAppointmentAction {
    LOCK,
    TAKE,
    RELEASE
  }

  public enum AddAppointmentRejectionReason {
    CLINIC_CLOSED,
    IN_THE_PAST,
    OVERLAPPING
  }

  public enum StaffAssignmentAction {
    ASSIGN,
    UNASSIGN
  }

  public enum GreenPassStatus {
    VACCINATED,
    TESTED,
    NOT_VACCINATED_OR_TESTED
  }
}
