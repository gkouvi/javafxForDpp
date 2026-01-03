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

    @FXML private TextField bimModelRefField;
    @FXML private ComboBox<String> bimFormatCombo;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField siteIdField;

    private final BuildingServiceClient buildingClient = new BuildingServiceClient();

    private Long siteId;

    public void setSiteId(Long id) {
        this.siteId = id;

        if (id == null) {
            siteIdField.setText("Δεν έχει επιλεγεί Μονάδα");
        } else {
            siteIdField.setText(id.toString());
            bimFormatCombo.getItems().addAll("IFC", "RVT", "DWG");
        }
    }

    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    public void onCreate() {
        try {
            // 1. ΔΗΜΙΟΥΡΓΙΑ ΚΤΙΡΙΟΥ
            BuildingModel b = new BuildingModel();
            b.setName(nameField.getText());
            b.setAddress(addressField.getText());
            b.setSiteId(siteId);




            BuildingModel created = buildingClient.createBuilding(b);
            loadBuilding(b);

            Long newId = created.getId();

            // 2. ΔΗΜΙΟΥΡΓΙΑ QR
            String payload = "DPP://building/" + newId;
            if (payload == null || !payload.startsWith("DPP://")) {
                throw new IllegalArgumentException("Invalid DPP QR");
            }

            // DPP://device/42
            String[] parts = payload.replace("DPP://", "").split("/");

            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid DPP format");
            }
            BufferedImage qr = QRUtil.generateQRCode(payload, 300);

            // 3. Ανέβασμα QR
            buildingClient.uploadBuildingQr(newId, qr);

            // 4. ΑΝΑΝΕΩΣΗ ΔΕΝΤΡΟΥ
            MainController.instance.refreshBuildingsForSite(siteId);

            // 5. ΚΛΕΙΣΙΜΟ
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadBuilding(BuildingModel b) {
        bimModelRefField.setText(b.getBimModelRef());
        bimFormatCombo.setValue(b.getBimFormat());
    }

}
