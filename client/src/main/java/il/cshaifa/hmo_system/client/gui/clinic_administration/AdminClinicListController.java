package il.cshaifa.hmo_system.client.gui.clinic_administration;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.EditClinicEvent;
import il.cshaifa.hmo_system.client.events.EditClinicEvent.Phase;
import il.cshaifa.hmo_system.client.gui.ResourcePath;
import il.cshaifa.hmo_system.entities.Clinic;
import il.cshaifa.hmo_system.messages.ClinicMessage;
import il.cshaifa.hmo_system.messages.Message.messageType;
import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AdminClinicListController extends Controller {

  public AdminClinicListController(ViewController view_controller) throws IOException {
    super(view_controller);
    EventBus.getDefault().register(this);
    HMOClient.getClient().getClinics();
  }

  @Subscribe
  public void editClinicRequestReceived(EditClinicEvent event) throws IOException {
    if (event.phase == Phase.SEND) return;

    // Navigate to AdminClinicView
    FXMLLoader loader =
        new FXMLLoader(
            getClass().getResource(ResourcePath.get_fxml(AdminClinicViewController.class)));

    loader.setControllerFactory(
        c -> {
          return new AdminClinicViewController(event.clinic);
        });

    Scene scene = new Scene(loader.load());

    var c = new AdminClinicController(loader.getController());

    Stage stage = new Stage();
    stage.setScene(scene);
    stage.show();
  }

  @Subscribe
  public void clinicsReceived(ClinicMessage message) {
    if (message.message_type != messageType.RESPONSE) return;
    ((AdminClinicListViewController) this.view_controller)
        .populateClinicTable((ArrayList<Clinic>) message.clinics);
  }
}
