package il.cshaifa.hmo_system.client.events;

import il.cshaifa.hmo_system.entities.Response;

public class ResponseEvent {
  public Response response;

  public ResponseEvent(Response response) {
    this.response = response;
  }
}