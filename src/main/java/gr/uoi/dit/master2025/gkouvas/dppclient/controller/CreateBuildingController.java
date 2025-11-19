package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.QRUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class CreateBuildingController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField siteIdField;

    private final BuildingServiceClient buildingClient = new BuildingServiceClient();

    private Long siteId;

    public void setSiteId(Long id) {
        this.siteId = id;

        if (id == null) {
            siteIdField.setText("No site selected");
        } else {
            siteIdField.setText(id.toString());
        }
    }

    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    public void onCreate() {
        try {
            // 1. CREATE BUILDING
            BuildingModel b = new BuildingModel();
            b.setName(nameField.getText());
            b.setAddress(addressField.getText());
            b.setSiteId(siteId);

            BuildingModel created = buildingClient.createBuilding(b);

            Long newId = created.getId();

            // 2. GENERATE QR
            String qrUrl = "http://localhost:8080/buildings/qr/" + newId;
            BufferedImage qr = QRUtil.generateQRCode(qrUrl, 300);

            // 3. UPLOAD QR
            buildingClient.uploadBuildingQr(newId, qr);

            // 4. REFRESH TREE
            MainController.instance.refreshBuildingsForSite(siteId);

            // 5. CLOSE
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
