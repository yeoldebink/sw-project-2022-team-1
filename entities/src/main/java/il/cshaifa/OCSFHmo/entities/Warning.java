package il.cshaifa.OCSFHmo.entities;

import java.io.Serializable;
import java.time.LocalTime;

public class Warning implements Serializable {

  private String message;
  private final LocalTime time;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Warning(String message) {
    this.message = message;
    this.time = LocalTime.now();
  }

  public LocalTime getTime() {
    return time;
  }
}
