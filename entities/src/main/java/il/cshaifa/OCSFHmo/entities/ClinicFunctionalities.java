package il.ac.haifa.client_server.entities.src.main.java.il.cshaifa.OCSFHmo.entities;

import java.io.Serializable;


public class ClinicFunctionalities implements Serializable {

	private final int functionality;
	private final int clinic_id;
	private final int day;
	private final String openingHours;

	public enum FuncList{
		GET_CLINICS_LIST,
		GET_CLINIC,
		UPDATE_CLINIC_HOURS
	}

	public ClinicFunctionalities(int functionality, int clinic_id, int day, String openingHours) {
		this.functionality = functionality;
		this.clinic_id = clinic_id;
		this.day = day;
		this.openingHours = openingHours;
	}

	public int getFunctionality() {
		return functionality;
	}

	public int getClinic_id() {
		return clinic_id;
	}

	public int getDay() {
		return day;
	}

	public String getOpeningHours() {
		return openingHours;
	}
}

