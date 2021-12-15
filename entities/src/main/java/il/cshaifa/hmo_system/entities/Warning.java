package il.cshaifa.hmo_system.entities;

import java.io.Serializable;
import java.time.LocalTime;

public class Warning implements Serializable {

  public static final long serialVersionUID = 11997114110L;

  private final LocalTime time;
  private String message;

  public Warning(String message) {
    this.message = message;
    this.time = LocalTime.now();
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalTime getTime() {
    return time;
  }
}
