package il.cshaifa.hmo_system.on_site_client.gui.patient;

import il.cshaifa.hmo_system.CommonEnums.OnSiteLoginAction;
import il.cshaifa.hmo_system.client_base.base_controllers.Controller;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.client_base.utils.Utils;
import il.cshaifa.hmo_system.entities.Patient;
import il.cshaifa.hmo_system.entities.User;
import il.cshaifa.hmo_system.on_site_client.App;
import il.cshaifa.hmo_system.on_site_client.HMOOnSiteClient;
import il.cshaifa.hmo_system.on_site_client.events.CloseStationEvent;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteEntryEvent;
import il.cshaifa.hmo_system.on_site_client.events.OnSiteLoginEvent;
import il.cshaifa.hmo_system.on_site_client.gui.login.OnSiteLoginController;
import il.cshaifa.hmo_system.on_site_client.gui.login.OnSiteLoginViewController;
import il.cshaifa.hmo_system.structs.QueuedAppointment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.greenrobot.eventbus.Subscribe;

public class OnSitePatientController extends Controller {

  private Patient patient;

  public OnSitePatientController(
      ViewController view_controller,
      Stage stage) {
    super(view_controller, stage);
    stage.initStyle(StageStyle.UNDECORATED);
  }

  @Subscribe
  public void onEntryEvent(OnSiteEntryEvent event) {
    try {
      if (event.getSender().equals(this.view_controller)) {
        HMOOnSiteClient.getClient().patientEntryRequest(event.id);

      } else if (event.getSender().equals(HMOOnSiteClient.getClient())) {
        this.patient = event.patient;
        if (event.patient == null) {
          Platform.runLater(() -> ((OnSitePatientViewController) this.view_controller).invalidID());
        } else if (event.q_appt != null) Platform.runLater(() -> printNumber(event.q_appt));
        else if (!event.patient.getHome_clinic().equals(HMOOnSiteClient.getClient().getStationClinic())) {
          Platform.runLater(() -> ((OnSitePatientViewController) this.view_controller).notInClinic(event.patient.getHome_clinic()));
        } else {
          Platform.runLater(() -> ((OnSitePatientViewController) this.view_controller).showDashboard(
              this.patient));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onOnSiteLoginEvent(OnSiteLoginEvent event) throws IOException {
    if (event.getSender().equals(this.view_controller)) {

      FXMLLoader loader =
          new FXMLLoader(App.class.getResource(Utils.get_fxml(OnSiteLoginViewController.class)));

      Scene scene = new Scene(loader.load());
      Stage nstage = new Stage();
      nstage.setScene(scene);
      OnSiteLoginController c = new OnSiteLoginController(loader.getController(), nstage, event.action);

      nstage.initOwner(stage);
      nstage.initModality(Modality.WINDOW_MODAL);
      Platform.runLater(nstage::show);
      PauseTransition pt = new PauseTransition(Duration.seconds(30));
      pt.setOnFinished(e -> nstage.close());
      pt.play();
    }
  }

  @Subscribe
  public void onCloseStationEvent(CloseStationEvent event) {
    Platform.runLater(stage::close);
    onWindowClose();
  }

  private void printNumber(QueuedAppointment q_appt) {
    Stage nstage = new Stage();
    VBox vbox = new VBox();

    AnchorPane pane = new AnchorPane();
    ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream("print_number_bg.jpg")));
    pane.getChildren().add(imgView);
    pane.setStyle("-fx-base: #ffffff; -fx-font-family: \"Helvetica Bold\"; -fx-font-size: 24px;");
    pane.setPrefWidth(400);

    Scene scene = new Scene(pane);

    vbox.setPadding(new Insets(128, 20, 30, 30));
    vbox.setPrefWidth(400);
    pane.getChildren().add(vbox);

    vbox.setSpacing(15);
    vbox.setAlignment(Pos.TOP_CENTER);

    // generate QR
    ByteArrayOutputStream out =
        QRCode.from(String.valueOf(q_appt.place_in_line))
            .to(ImageType.PNG)
            .withSize(200, 200)
            .stream();
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

    vbox.getChildren().addAll(Arrays.asList(
        new Label("Number"),
        new Label(q_appt.place_in_line),
        new ImageView(new Image(in))
    ));

    User staff_member = q_appt.appointment.getStaff_member();
    if (staff_member != null) {
      vbox.getChildren().addAll(Arrays.asList(
          new Label(String.format("Dr. %s", staff_member)),
          new Label(staff_member.getRole().getName()),
          new Label(Utils.prettifyDateTime(q_appt.appointment.getDate()))
      ));
    } else {
      vbox.getChildren().add(new Label(q_appt.appointment.getType().getName()));
      if (Arrays.asList("COVID Test", "COVID Vaccine", "Flu Vaccine").contains(q_appt.appointment.getType().getName())) {
        vbox.getChildren().add(new Label(Utils.prettifyDateTime(q_appt.appointment.getDate())));
      }
    }

    nstage.setScene(scene);
    nstage.initOwner(stage);
    nstage.initModality(Modality.WINDOW_MODAL);
    nstage.initStyle(StageStyle.UNDECORATED);

    nstage.show();
    PauseTransition pt = new PauseTransition(Duration.seconds(10));
    pt.setOnFinished(actionEvent -> {
      nstage.close();
      ((OnSitePatientViewController) view_controller).returnToEntryScreen();
    });
    pt.play();
  }
}
