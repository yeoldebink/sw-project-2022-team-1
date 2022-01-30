package il.cshaifa.hmo_system;

import il.cshaifa.hmo_system.entities.AppointmentType;
import il.cshaifa.hmo_system.entities.Role;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Constants implements Serializable {

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
  public static final String LAB_TEST = "Lab Test";

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
      put(getApptType(FAMILY_DOCTOR), 4L);
      put(getApptType(PEDIATRICIAN), 4L);
      put(getApptType(COVID_TEST), 4L);
      put(getApptType(COVID_VACCINE), 4L);
      put(getApptType(FLU_VACCINE), 4L);
      put(getApptType(SPECIALIST), 12L);
    }};

    GENERAL_PHYSICAN = new HashSet<>(Arrays.asList(
        getApptType(FAMILY_DOCTOR),
        getApptType(PEDIATRICIAN))
    );

    UNSTAFFED_APPT_TYPES = new HashSet<>(Arrays.asList(
        getApptType(NURSE),
        getApptType(LAB_TEST),
        getApptType(COVID_TEST),
        getApptType(COVID_VACCINE),
        getApptType(FLU_VACCINE)
    ));

    APPT_DURATION = new HashMap<>() {{
      for (var appt_type : UNSTAFFED_APPT_TYPES) {
        put(appt_type, 10L);
      }

      for (var appt_type : GENERAL_PHYSICAN) {
        put(appt_type, 15L);
      }

      put(getApptType(SPECIALIST), 20L);
    }};

    WALK_IN_ROLES = new HashSet<>(
        Arrays.asList(
            getRole(NURSE),
            getRole(LAB_TECHNICIAN)
        )
    );
  }

  public static AppointmentType getApptType(String name) {
    return APPOINTMENT_TYPES.get(name);
  }

  public static Role getRole(String name) {
    return ROLES.get(name);
  }

  public static HashMap<String, AppointmentType> APPOINTMENT_TYPES;
  public static HashMap<String, Role> ROLES;

  // Maps
  public static Map<AppointmentType, Long> FUTURE_APPT_CUTOFF_WEEKS;
  public static Map<AppointmentType, Long> APPT_DURATION;
  public static HashSet<AppointmentType> GENERAL_PHYSICAN;
  public static HashSet<AppointmentType> UNSTAFFED_APPT_TYPES;
  public static HashSet<Role> WALK_IN_ROLES;
}

