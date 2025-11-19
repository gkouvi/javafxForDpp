package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.SiteModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.QRUtil;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class CreateSiteController {

    @FXML private TextField nameField;
    @FXML private TextField regionField;
    @FXML private TextField coordField;

    private final SiteServiceClient siteClient = new SiteServiceClient();

    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    public void onCreate() {
        try {
            // VALIDATION
            if (nameField.getText().isBlank()) {
                showError("Το όνομα της Μονάδας είναι υποχρεωτικό");
                return;
            }

            // 1. Create Site (WITHOUT QR)
            SiteModel s = new SiteModel();
            s.setName(nameField.getText());
            s.setRegion(regionField.getText());
            s.setCoordinates(coordField.getText());

            SiteModel created = siteClient.createSite(s);
            Long newId = created.getId();

            // 2. Generate QR in client
            String qrUrl = "http://localhost:8080/sites/qr/" + newId;
            BufferedImage qr = QRUtil.generateQRCode(qrUrl, 300);

            // 3. Upload QR to backend
            siteClient.uploadSiteQr(newId, qr);

            // 4. Refresh tree
            MainController.instance.refreshSites();

            // 5. Close window
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
