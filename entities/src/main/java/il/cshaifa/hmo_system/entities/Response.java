package il.cshaifa.hmo_system.entities;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
  public static final long serialVersionUID = 114101115112L;
  public ResponseType response_type;
  public boolean confirmed;
  public List<?> results;
  public Response(ResponseType response_type, boolean confirmed, List<?> results) {
    this.response_type = response_type;
    this.confirmed = confirmed;
    this.results = results;
  }

  public enum ResponseType {
    CONFIRM_UPDATE,
    QUERY_RESULTS
  }
}
