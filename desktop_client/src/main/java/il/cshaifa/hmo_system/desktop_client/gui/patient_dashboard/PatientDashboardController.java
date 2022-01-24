package il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard;

import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.events.AppointmentListEvent;
import il.cshaifa.hmo_system.client_base.utils.Utils;
import il.cshaifa.hmo_system.desktop_client.HMODesktopClient;
import il.cshaifa.hmo_system.desktop_client.events.GreenPassStatusEvent;
import il.cshaifa.hmo_system.desktop_client.events.MyClinicEvent;
import il.cshaifa.hmo_system.desktop_client.events.NextAppointmentEvent;
import il.cshaifa.hmo_system.desktop_client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.appointments.SetAppointmentController;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.appointments.SetAppointmentViewController;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.clinic_view.MyClinicController;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.clinic_view.MyClinicViewController;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.green_pass.GreenPassController;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.green_pass.GreenPassViewController;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.patient_history.PatientAppointmentHistoryListController;
import il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.patient_history.PatientAppointmentHistoryListViewController;
import il.cshaifa.hmo_system.entities.Appointment;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

public class PatientDashboardController extends Controller {

  private Appointment nextAppointment;

  public PatientDashboardController(ViewController view_controller, Stage stage) {
    super(view_controller, null);

    try {
      HMODesktopClient.getClient().getPatientNextAppointment();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  @Override
  public void onWindowClose() {
    super.onWindowClose();
  }

  @Subscribe
  public void onSetAppointmentEvent(SetAppointmentEvent event) {
    if (event.getSender() == this.view_controller) { // open the set appointments window
      Utils.openNewSingletonWindow(
          SetAppointmentViewController.class,
          SetAppointmentController.class,
          false,
          c ->
              new SetAppointmentViewController(
                  HMODesktopClient.getClient().getConnected_patient()));
    }
  }

  @Subscribe
  public void onViewClinicEvent(MyClinicEvent event) {
    Utils.openNewSingletonWindow(
        MyClinicViewController.class,
        MyClinicController.class,
        false,
        c ->
            new MyClinicViewController(
                HMODesktopClient.getClient().getConnected_patient().getHome_clinic()));
  }

  @Subscribe
  public void onPatientHistoryRequest(AppointmentListEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    FXMLLoader loader =
        new FXMLLoader(
            getClass()
                .getResource(Utils.get_fxml(PatientAppointmentHistoryListViewController.class)));

    Utils.openNewSingletonWindow(
        PatientAppointmentHistoryListViewController.class,
        PatientAppointmentHistoryListController.class,
        false,
        c ->
            new PatientAppointmentHistoryListViewController(
                HMODesktopClient.getClient().getConnected_patient()));
  }

  @Subscribe
  public void onNextAppointmentEvent(NextAppointmentEvent event) {
    if (event.getSender().equals(HMODesktopClient.getClient())) {
      Platform.runLater(
          () ->
              ((PatientDashboardViewController) view_controller)
                  .updateNextAppointmentInfo(event.appointment));
    }
  }

  @Subscribe
  public void onGreenPassStatusEvent(GreenPassStatusEvent event) {
    if (event.getSender() == this.view_controller) {
      try {
        HMODesktopClient.getClient().getGreenPassStatus();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    } else {
      var loader =
          new FXMLLoader(getClass().getResource(Utils.get_fxml(GreenPassViewController.class)));

      Utils.openNewSingletonWindow(
          GreenPassViewController.class,
          GreenPassController.class,
          false,
          c -> new GreenPassViewController(event));
    }
  }
}
