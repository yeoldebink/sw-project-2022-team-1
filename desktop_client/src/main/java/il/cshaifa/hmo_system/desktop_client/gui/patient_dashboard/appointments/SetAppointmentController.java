package il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.appointments;

import static il.cshaifa.hmo_system.Constants.COVID_TEST;
import static il.cshaifa.hmo_system.Constants.COVID_VACCINE;
import static il.cshaifa.hmo_system.Constants.FAMILY_DOCTOR;
import static il.cshaifa.hmo_system.Constants.FLU_VACCINE;
import static il.cshaifa.hmo_system.Constants.PEDIATRICIAN;
import static il.cshaifa.hmo_system.Constants.SPECIALIST;

import il.cshaifa.hmo_system.CommonEnums.SetAppointmentAction;
import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.events.AppointmentListEvent;
import il.cshaifa.hmo_system.desktop_client.HMODesktopClient;
import il.cshaifa.hmo_system.desktop_client.events.SetAppointmentEvent;
import il.cshaifa.hmo_system.desktop_client.events.SetAppointmentEvent.ResponseType;
import il.cshaifa.hmo_system.entities.AppointmentType;
import java.io.IOException;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.greenrobot.eventbus.Subscribe;

public class SetAppointmentController extends Controller {

  private static SetAppointmentController instance;

  private SetAppointmentController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    try {
      HMODesktopClient.getClient().getSpecialistRoles();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  public static SetAppointmentController getInstance() {
    return instance;
  }

  public static void create(ViewController view_controller, Stage stage) {
    if (instance != null && instance.view_controller != null) return;
    instance = new SetAppointmentController(view_controller, stage);
  }

  private boolean patientIsMinor() {
    return HMODesktopClient.getClient()
        .getConnected_patient()
        .getBirthday()
        .isAfter(LocalDateTime.now().minusYears(18));
  }

  @Subscribe
  public void onAppointmentsRequested(SetAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller) || event.action != null) return;
    try {
      switch (event.appointmentType.getName()) {
        case FAMILY_DOCTOR:
          HMODesktopClient.getClient()
              .getClinicAppointments(
                  new AppointmentType(patientIsMinor() ? PEDIATRICIAN : FAMILY_DOCTOR));
          break;

        case SPECIALIST:
          HMODesktopClient.getClient().getSpecialistAppointments(event.role);
          break;

        case COVID_VACCINE:
        case FLU_VACCINE:
        case COVID_TEST:
          HMODesktopClient.getClient().getClinicAppointments(event.appointmentType);
          break;
        default:
          throw new NotImplementedException(
              String.format("Appointment type request not implemented: %s", event.appointmentType));
      }
    } catch (IOException | NotImplementedException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onAppointmentListEvent(AppointmentListEvent event) {
    Platform.runLater(
        () ->
            ((SetAppointmentViewController) view_controller)
                .populateAppointments(event.appointments));
  }

  @Subscribe
  public void onAppointmentSelectionChanged(SetAppointmentEvent event) {
    if (!event.getSender().equals(this.view_controller) || event.action == null) return;
    try {
      switch (event.action) {
        case RELEASE:
          HMODesktopClient.getClient().cancelAppointment(event.appointment);
          break;
        case LOCK:
          HMODesktopClient.getClient().lockAppointment(event.appointment);
          break;
        case TAKE:
          HMODesktopClient.getClient().takeAppointment(event.appointment);
          break;
        default:
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onResponseFromClient(SetAppointmentEvent event) {
    if (!event.getSender().equals(HMODesktopClient.getClient())) return;
    if (event.action == SetAppointmentAction.TAKE) {
      Platform.runLater(
          () ->
              ((SetAppointmentViewController) view_controller)
                  .takeAppointment(
                      event.response == ResponseType.AUTHORIZE,
                      (int) stage.getX() + 100,
                      (int) stage.getY() + 100));

      try {
        HMODesktopClient.getClient().getPatientNextAppointment();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    } else if (event.specialistRoles != null) {
      ((SetAppointmentViewController) view_controller)
          .populateSpecialistRoles(event.specialistRoles);
    }
  }
}
