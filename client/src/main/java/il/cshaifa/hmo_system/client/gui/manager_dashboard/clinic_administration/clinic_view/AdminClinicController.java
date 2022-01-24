package il.cshaifa.hmo_system.client.gui.manager_dashboard.clinic_administration.clinic_view;

import il.cshaifa.hmo_system.client.HMOClient;
import il.cshaifa.hmo_system.client.base_controllers.Controller;
import il.cshaifa.hmo_system.client.base_controllers.ViewController;
import il.cshaifa.hmo_system.client.events.ClinicEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;
import org.kordamp.ikonli.javafx.FontIcon;

public class AdminClinicController extends Controller {

  public AdminClinicController(ViewController view_controller, Stage stage) {
    super(view_controller, stage);
    stage.initModality(Modality.APPLICATION_MODAL);
  }

  /**
   * Event to handle user request to update clinic data with changes in the view
   *
   * @param event Clinic object created from the GUI changes.
   */
  @Subscribe
  public void onRequestClinicUpdate(ClinicEvent event) {
    if (!event.getSender().equals(this.view_controller)) return;

    // validate hours
    String[] hours = {
      event.clinic.getSun_hours(),
      event.clinic.getMon_hours(),
      event.clinic.getTue_hours(),
      event.clinic.getWed_hours(),
      event.clinic.getThu_hours(),
      event.clinic.getFri_hours(),
      event.clinic.getSat_hours()
    };

    DateTimeFormatter hoursFormat = DateTimeFormatter.ofPattern("H:m");

    for (var hoursRangeString : hours) {
      if (hoursRangeString == null || hoursRangeString.equals("")) continue;
      var splitComma = hoursRangeString.split(", ");
      for (var h : splitComma) {
        var splitHyphen = h.split("-");
        try {
          assert splitHyphen.length == 2;
          LocalTime.parse(splitHyphen[0], hoursFormat);
          LocalTime.parse(splitHyphen[1], hoursFormat);
        } catch (DateTimeParseException | AssertionError e) {
          invalidHours();
          return;
        }
      }
    }

    var client = HMOClient.getClient();
    try {
      client.updateClinic(event.clinic);
      client.getClinics();
    } catch (IOException e) {
      e.printStackTrace();
    }

    stage.close();
  }

  private void invalidHours() {
    Stage nStage = new Stage();
    nStage.initModality(Modality.APPLICATION_MODAL);
    nStage.setX(stage.getX() + 50);
    nStage.setY(stage.getY() + 100);

    VBox vbox = new VBox();
    vbox.setSpacing(10);
    vbox.setPadding(new Insets(10, 10, 10, 10));
    Label label = new Label("Clinic hours format invalid.\nMust be HH:MM-HH:MM, HH:MM-HH:MM,...");
    FontIcon icon = new FontIcon();
    icon.setIconLiteral("mdi-alert-outline");
    icon.setIconSize(30);
    icon.setIconColor(Color.DARKRED);
    label.setGraphic(icon);
    vbox.getChildren().add(label);

    HBox hbox = new HBox();
    hbox.setAlignment(Pos.CENTER);
    Button ok = new Button("Okay");
    ok.setOnAction((event) -> nStage.close());
    hbox.getChildren().add(ok);
    vbox.getChildren().add(hbox);

    Scene scene = new Scene(vbox);
    nStage.setScene(scene);
    nStage.showAndWait();
  }
}
