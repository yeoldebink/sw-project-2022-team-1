package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.report_view;

public class ReportDataRow {
  private final String key;
  private final Double value;

  public ReportDataRow(String key, Double value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public Double getValue() {
    return value;
  }
}
