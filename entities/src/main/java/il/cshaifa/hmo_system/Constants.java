package il.cshaifa.hmo_system;

import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Role;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class contains all the constants necessary for the system's operation.
 * It is initialized using database values in the server; once this is done
 * the values for its init (a list of appointment types and roles) are
 * sent to the client for local initialization. This ensures uniform behavior
 * between client and server applications provided builds are current as well
 * as (relatively) simple addition of roles, appointment types, and additional
 * parameters as clinic policies change.
 */
public class Constants {

  // roles & appt_types
  public static final String PATIENT = "Patient";
  public static final String HMO_MANAGER = "HMO Manager";
  public static final String CLINIC_MANAGER = "Clinic Manager";
  public static final String NURSE = "Nurse";
  public static final String LAB_TECHNICIAN = "Lab Technician";
  public static final String NEUROLOGIST = "Neurologist";
  public static final String ENDOCRINOLOGIST = "Endocrinologist";
  public static final String DERMATOLOGIST = "Dermatologist";
  public static final String ORTHOPEDIST = "Orthopedist";
  public static final String CARDIOLOGIST = "Cardiologist";
  public static final String FAMILY_DOCTOR = "Family Doctor";
  public static final String PEDIATRICIAN = "Pediatrician";
  public static final String COVID_TEST = "COVID Test";
  public static final String COVID_VACCINE = "COVID Vaccine";
  public static final String FLU_VACCINE = "Flu Vaccine";
  public static final String SPECIALIST = "Specialist";
  public static final String LAB_TESTS = "Lab Tests";

  // column names
  public static final String NAME_COL = "name";
  public static final String APPT_DATE_COL = "appt_date";
  public static final String ARRIVED_COL = "arrived";
  public static final String CALLED_TIME_COL = "called_time";
  public static final String CLINIC_COL = "clinic";
  public static final String COMMENTS_COL = "comments";
  public static final String ID_COL = "id";
  public static final String LOCK_TIME_COL = "lock_time";
  public static final String PATIENT_COL = "patient";
  public static final String SPECIALIST_ROLE_COL = "specialist_role";
  public static final String STAFF_MEMBER_COL = "staff_member";
  public static final String TAKEN_COL = "taken";
  public static final String TYPE_COL = "type";
  public static final String USER_COL = "user";
  public static final String ADDRESS_COL = "address";
  public static final String FRI_HOURS_COL = "fri_hours";
  public static final String MANAGER_USER_COL = "manager_user";
  public static final String MON_HOURS_COL = "mon_hours";
  public static final String SAT_HOURS_COL = "sat_hours";
  public static final String SUN_HOURS_COL = "sun_hours";
  public static final String THU_HOURS_COL = "thu_hours";
  public static final String TUE_HOURS_COL = "tue_hours";
  public static final String WED_HOURS_COL = "wed_hours";
  public static final String BIRTHDAY_COL = "birthday";
  public static final String HOME_CLINIC_COL = "home_clinic";
  public static final String IS_SPECIALIST_COL = "is_specialist";
  public static final String EMAIL_COL = "email";
  public static final String FIRSTNAME_COL = "firstName";
  public static final String LASTNAME_COL = "lastName";
  public static final String PASSWORD_COL = "password";
  public static final String PHONE_COL = "phone";
  public static final String ROLE_COL = "role";
  public static final String SALT_COL = "salt";

  public static void init(List<AppointmentType> appt_types, List<Role> roles) {
    APPOINTMENT_TYPES = new HashMap<>() {{
      for (var appt_type : appt_types) {
        put(appt_type.getName(), appt_type);
      }
    }};

    ROLES = new HashMap<>() {{
      for (var role : roles) {
        put(role.getName(), role);
      }
    }};

    // init maps
    FUTURE_APPT_CUTOFF_WEEKS = new HashMap<>() {{
      put(APPT_TYPE(FAMILY_DOCTOR), 4L);
      put(APPT_TYPE(PEDIATRICIAN), 4L);
      put(APPT_TYPE(COVID_TEST), 4L);
      put(APPT_TYPE(COVID_VACCINE), 4L);
      put(APPT_TYPE(FLU_VACCINE), 4L);
      put(APPT_TYPE(SPECIALIST), 12L);
    }};

    GENERAL_PHYSICAN = new HashSet<>(Arrays.asList(
        APPT_TYPE(FAMILY_DOCTOR),
        APPT_TYPE(PEDIATRICIAN))
    );

    UNSTAFFED_APPT_TYPES = new HashSet<>(Arrays.asList(
        APPT_TYPE(NURSE),
        APPT_TYPE(LAB_TESTS),
        APPT_TYPE(COVID_TEST),
        APPT_TYPE(COVID_VACCINE),
        APPT_TYPE(FLU_VACCINE)
    ));

    UNSTAFFED_NON_WALK_IN_APPT_TYPES = new HashSet<>(Arrays.asList(
        APPT_TYPE(COVID_TEST),
        APPT_TYPE(COVID_VACCINE),
        APPT_TYPE(FLU_VACCINE)
    ));

    APPT_DURATION = new HashMap<>() {{
      for (var appt_type : UNSTAFFED_APPT_TYPES) {
        put(appt_type, 10L);
      }

      for (var appt_type : GENERAL_PHYSICAN) {
        put(appt_type, 15L);
      }

      put(APPT_TYPE(SPECIALIST), 20L);
    }};

    WALK_IN_ROLES = new HashSet<>(
        Arrays.asList(
            ROLE(NURSE),
            ROLE(LAB_TECHNICIAN)
        )
    );

    MANAGER_ROLES = new HashSet<>(Arrays.asList(ROLE(HMO_MANAGER), ROLE(CLINIC_MANAGER)));
  }

  public static AppointmentType APPT_TYPE(String name) {
    return APPOINTMENT_TYPES.get(name);
  }

  public static Role ROLE(String name) {
    return ROLES.get(name);
  }

  public static HashMap<String, AppointmentType> APPOINTMENT_TYPES;
  public static HashMap<String, Role> ROLES;

  // Maps
  public static Map<AppointmentType, Long> FUTURE_APPT_CUTOFF_WEEKS;
  public static Map<AppointmentType, Long> APPT_DURATION;
  public static HashSet<AppointmentType> GENERAL_PHYSICAN;
  public static HashSet<AppointmentType> UNSTAFFED_APPT_TYPES;
  public static HashSet<AppointmentType> UNSTAFFED_NON_WALK_IN_APPT_TYPES;
  public static HashSet<Role> WALK_IN_ROLES;
  public static HashSet<Role> MANAGER_ROLES;
}

