package il.cshaifa.hmo_system.entities;

/**
 * Represents a request sent from the client to the server If is_update is true, the server should
 * update the database row specified by entity. If not, the server returns a "select * from <entity
 * table>" query.
 *
 * <p>This is an interim solution to the OCSF model and will be updated later on.
 */
public class Request {
  private final boolean is_update;
  private final Object entity;

  public Request(boolean is_update, Object entity) {
    this.is_update = is_update;
    this.entity = entity;
  }

  public boolean isUpdate() {
    return is_update;
  }

  public Object getEntity() {
    return entity;
  }
}
