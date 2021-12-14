package il.cshaifa.OCSFHmo.entities;

public class ClinicRequestBuilder {

  private int functionality;
  private int clinic_id = 0;
  private int day = 0;
  String openingHours = "";

  public ClinicFunctionalities buildRequest() {
    return new ClinicFunctionalities(functionality, clinic_id, day, openingHours);
  }

  public ClinicRequestBuilder functionality(int functionality) {
    this.functionality = functionality;
    return this;
  }

  public ClinicRequestBuilder clinicId(int clinic_id) {
    this.clinic_id = clinic_id;
    return this;
  }

  public ClinicRequestBuilder day(int day) {
    this.day = day;
    return this;
  }

  public ClinicRequestBuilder OpeningHours(String openingHours) {
    this.openingHours = openingHours;
    return this;
  }
}
