package il.cshaifa.hmo_system.client.gui;

/**
 * Gets FXML paths based on the gui class name. Depends on keeping FXML names and ViewController
 * names the same, i.e. AdminClinicListViewController must correspond to AdminClinicListView.fxml
 */
public class ResourcePath {
  public static String get_fxml(Class<?> view_controller_class) {
    String canonicalName = view_controller_class.getCanonicalName();
    String path = "/" + canonicalName.replace(".", "/").replace("Controller", "");
    return path + ".fxml";
  }
}
