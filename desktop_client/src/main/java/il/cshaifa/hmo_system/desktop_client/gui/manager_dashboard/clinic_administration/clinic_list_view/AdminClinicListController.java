package il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_list_view;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.events.ClinicEvent;
import il.cshaifa.hmo_system.client_base.utils.ClientUtils;
import il.cshaifa.hmo_system.desktop_client.HMODesktopClient;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_view.AdminClinicController;
import il.cshaifa.hmo_system.desktop_client.gui.manager_dashboard.clinic_administration.clinic_view.AdminClinicViewController;
import il.cshaifa.hmo_system.entities.Clinic;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.greenrobot.eventbus.Subscribe;

public class AdminClinicListController extends Controller {

  public AdminClinicListController(ViewController view_controller) {
    super(view_controller, null);
  }

  /**
   * Event to handle the user request to open the Edit clinic window
   *
   * @param event Holds GUI data on the relevant clinic to edit
   */
  @Subscribe
  public void onShowEditClinicDialog(ClinicEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    // Navigate to AdminClinicView
    FXMLLoader loader =
        new FXMLLoader(
            getClass().getResource(ClientUtils.get_fxml(AdminClinicViewController.class)));

    // this is for the HMO manager
    if (event.clinic == null) {
      event.clinic = HMODesktopClient.getClient().getConnected_employee_clinics().get(0);
    }

    loader.setControllerFactory(
        c -> {
          return new AdminClinicViewController(
              event.clinic, HMODesktopClient.getClient().getConnected_user().getRole());
        });
    try {
      ClientUtils.openNewWindow(
          AdminClinicViewController.class, AdminClinicController.class, loader, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the view with the updated clinic list
   *
   * @param clinics list of updated clinics
   */
  public void updateClinics(ArrayList<Clinic> clinics) {
    Platform.runLater(
        () -> ((AdminClinicListViewController) this.view_controller).populateClinicTable(clinics));
  }
}
