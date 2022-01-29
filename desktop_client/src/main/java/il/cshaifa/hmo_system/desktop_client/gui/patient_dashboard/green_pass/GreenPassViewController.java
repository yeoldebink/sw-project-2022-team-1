package il.cshaifa.hmo_system.desktop_client.gui.patient_dashboard.green_pass;

import il.cshaifa.hmo_system.CommonEnums.GreenPassStatus;
import il.cshaifa.hmo_system.client_base.base_controllers.ViewController;
import il.cshaifa.hmo_system.Utils;
import il.cshaifa.hmo_system.desktop_client.HMODesktopClient;
import il.cshaifa.hmo_system.desktop_client.events.GreenPassStatusEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class GreenPassViewController extends ViewController {

  GreenPassStatus status;
  LocalDateTime lastVaccineDate, lastTestDate;

  @FXML private ImageView bannerImageView;

  @FXML private Label infoLabel;

  @FXML private ImageView qrCodeImageView;

  public GreenPassViewController(GreenPassStatusEvent statusEvent) {
    this.status = statusEvent.status;
    this.lastVaccineDate = statusEvent.last_vaccine;
    this.lastTestDate = statusEvent.last_covid_test;
  }

  @FXML
  public void initialize() {

    String imageName = null;

    if (status == GreenPassStatus.REJECT) {
      imageName = "no_green_pass.jpg";
    } else {
      imageName = "your_green_pass.jpg";
      // generate QR
      ByteArrayOutputStream out =
          QRCode.from(String.valueOf((HMODesktopClient.getClient().getConnected_user().getId())))
              .to(ImageType.PNG)
              .withSize(200, 200)
              .stream();
      ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

      qrCodeImageView.setImage(new Image(in));
    }

    String expDateString = null;

    if (lastTestDate == null && lastVaccineDate == null) {
      expDateString = "N/A";
    } else {
      LocalDateTime vaccineExpiration = null, testExpiration = null;
      if (lastVaccineDate != null) {
        vaccineExpiration = lastVaccineDate.plusMonths(6);
      }

      if (lastTestDate != null) {
        testExpiration = lastTestDate.plusWeeks(1);
      }

      if (vaccineExpiration != null && testExpiration != null) {
        expDateString =
            Utils.prettifyDateTime(
                vaccineExpiration.isAfter(testExpiration) ? vaccineExpiration : testExpiration);
      } else
        expDateString =
            Utils.prettifyDateTime(vaccineExpiration == null ? testExpiration : vaccineExpiration);
    }

    infoLabel.setText(
        String.format(
            "Last COVID-19 vaccine: %s\nLast COVID-19 test: %s\nExpiration date: %s",
            lastVaccineDate == null ? "N/A" : Utils.prettifyDateTime(lastVaccineDate),
            lastTestDate == null ? "N/A" : Utils.prettifyDateTime(lastTestDate),
            expDateString));

    bannerImageView.setImage(new Image(getClass().getResourceAsStream(imageName)));
    bannerImageView.setFitWidth(430);
    bannerImageView.setFitHeight(74.1379);
  }
}
