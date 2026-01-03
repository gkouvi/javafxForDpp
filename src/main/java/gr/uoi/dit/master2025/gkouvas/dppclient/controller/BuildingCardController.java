package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class BuildingCardController {

    @FXML
    private Label buildingNameLabel;
    @FXML private Label addressLabel;

    @FXML private Label bimFormatLabel;
    @FXML private Label bimModelRefLabel;

    @FXML private ImageView qrImage;

    private final BuildingServiceClient buildingClient =
            new BuildingServiceClient();

    private Long buildingId;

    public void loadBuilding(Long buildingId) {
        this.buildingId = buildingId;

        BuildingModel b = buildingClient.getBuilding(buildingId);

        buildingNameLabel.setText(b.getName());
        addressLabel.setText(nullToDash(b.getAddress()));

        bimFormatLabel.setText(nullToDash(b.getBimFormat()));
        bimModelRefLabel.setText(nullToDash(b.getBimModelRef()));

        loadQr(b);
    }

    private void loadQr(BuildingModel b) {
        try {
            if (b.getQrBase64() == null) return;

            byte[] bytes = Base64.getDecoder().decode(b.getQrBase64());
            BufferedImage buffered =
                    ImageIO.read(new ByteArrayInputStream(bytes));

            qrImage.setImage(
                    SwingFXUtils.toFXImage(buffered, null)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }
}
