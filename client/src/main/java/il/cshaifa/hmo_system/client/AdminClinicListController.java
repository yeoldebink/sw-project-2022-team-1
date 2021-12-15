package il.cshaifa.hmo_system.client;

import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.EditClinicEvent;
import il.cshaifa.hmo_system.client.events.ResponseEvent;
import il.cshaifa.hmo_system.entities.Clinic;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AdminClinicListController extends Controller {

  public AdminClinicListController(ViewController view_controller) {
    super(view_controller);
    EventBus.getDefault().register(this);
  }

  @Subscribe
  public void editClinicRequestReceived(EditClinicEvent event) {}

  @Subscribe
  public void clinicsReceived(ResponseEvent event) {
    ((AdminClinicListViewController) this.view_controller)
        .populateClinicTable((ArrayList<Clinic>) event.response.results);
  }
}
