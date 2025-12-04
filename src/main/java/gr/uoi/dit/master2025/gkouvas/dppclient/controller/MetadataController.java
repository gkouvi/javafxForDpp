/*
package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.SiteModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

*/
/**
 * Controller for the Metadata tab.
 *
 * IMPORTANT:
 * - initialize() runs ONCE when FXML loads
 * - refresh(siteId, buildingId, deviceId) runs EVERY TIME the user selects a new item in TreeView
 *//*

public class MetadataController {

    @FXML private VBox infoBox;

    private final SiteServiceClient siteClient = new SiteServiceClient();
    private final BuildingServiceClient buildingClient = new BuildingServiceClient();
    private final DeviceServiceClient deviceClient = new DeviceServiceClient();
    @FXML private ImageView qrImage;

    */
/**
     * Runs once at initial FXML load.
     *//*

    @FXML
    public void initialize() {


        infoBox.getChildren().clear();
        infoBox.getChildren().add(new Label("No selection"));
    }

    */
/**
     * Called every time the user selects a new Site / Building / Device.
     *
     * Only this method loads fresh data from the backend.
     *//*

    public void refresh(Long siteId, Long buildingId, Long deviceId) {

        infoBox.getChildren().clear();

        if (deviceId != null) {
            loadDeviceMetadata(deviceId);
            return;
        }

        if (buildingId != null) {
            loadBuildingMetadata(buildingId);
            return;
        }

        if (siteId != null) {
            loadSiteMetadata(siteId);
            return;
        }

        infoBox.getChildren().add(new Label("No selection"));
    }

    // -------------------------------------------------------------------------
    //  LOADERS
    // -------------------------------------------------------------------------

    private void loadSiteMetadata(Long siteId) {
        SiteModel site = siteClient.getSite(siteId);

        infoBox.getChildren().add(new Label("Site Name: " + site.getName()));
        infoBox.getChildren().add(new Label("Region: " + site.getRegion()));
        infoBox.getChildren().add(new Label("Coordinates: " + site.getCoordinates()));
        infoBox.getChildren().add(new Label("Buildings: " + site.getBuildingIds().size()));
    }

    private void loadBuildingMetadata(Long buildingId) {
        BuildingModel b = buildingClient.getBuilding(buildingId);

        infoBox.getChildren().add(new Label("Building Name: " + b.getName()));
        infoBox.getChildren().add(new Label("Address: " + b.getAddress()));
        infoBox.getChildren().add(new Label("Site ID: " + b.getSiteId()));
    }

    private void loadDeviceMetadata(Long deviceId) {
        DeviceModel d = deviceClient.getDevice(deviceId);

        infoBox.getChildren().add(new Label("Device Name: " + d.getName()));
        infoBox.getChildren().add(new Label("Type: " + d.getType()));
        infoBox.getChildren().add(new Label("Serial Number: " + d.getSerialNumber()));
        infoBox.getChildren().add(new Label("Installation Date: " + d.getInstallationDate()));
        infoBox.getChildren().add(new Label("Firmware: " + d.getFirmwareVersion()));
        infoBox.getChildren().add(new Label("Status: " + d.getStatus()));
        infoBox.getChildren().add(new Label("Building ID: " + d.getBuildingId()));
    }

    @FXML
    public void onExportQr() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save QR");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
            File file = chooser.showSaveDialog(null);
            if (file == null) return;

            Image qr = qrImage.getImage();
            BufferedImage bImage = SwingFXUtils.fromFXImage(qr, null);
            ImageIO.write(bImage, "png", file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
*/
package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.SiteModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.print.PrinterJob;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Metadata tab controller (Site / Building / Device)
 * + QR code display + Export + Print
 */
public class MetadataController {

    private static final String BASE_URL = "https://192.168.0.105:8443";

    @FXML private VBox infoBox;
    @FXML private ImageView qrImage;
    @FXML private HBox qrButtonsBox;


    private final SiteServiceClient siteClient = new SiteServiceClient();
    private final BuildingServiceClient buildingClient = new BuildingServiceClient();
    private final DeviceServiceClient deviceClient = new DeviceServiceClient();


    @FXML
    public void initialize() {
        infoBox.getChildren().clear();
        infoBox.getChildren().add(new Label("Καμία επιλογή"));
        qrImage.setImage(null);
    }


    /**
     * Refresh metadata & QR code whenever the tree selection changes.
     */
    public void refresh(Long siteId, Long buildingId, Long deviceId) {

        infoBox.getChildren().clear();
        qrImage.setImage(null);

        // Default hide
        qrButtonsBox.setVisible(false);
        qrButtonsBox.setManaged(false);

        if (deviceId != null) {
            loadDeviceMetadata(deviceId);
            loadQR("/qr/device/" + deviceId);

            qrButtonsBox.setVisible(true);
            qrButtonsBox.setManaged(true);
            return;
        }

        if (buildingId != null) {
            loadBuildingMetadata(buildingId);
            loadQR("/qr/building/" + buildingId);

            qrButtonsBox.setVisible(true);
            qrButtonsBox.setManaged(true);
            return;
        }

// SITE → no QR
        if (siteId != null) {
            loadSiteMetadata(siteId);
            return;
        }


        infoBox.getChildren().add(new Label("Καμία επιλογή"));
    }



    // -------------------------------
    //  LOADERS
    // -------------------------------

    private void loadSiteMetadata(Long siteId) {

        // Φέρνουμε το site από backend
        SiteModel site = siteClient.getSite(siteId);

        // Καθαρίζουμε προηγούμενο UI
        infoBox.getChildren().clear();

        infoBox.getChildren().add(new Label("Όνομα Μονάδας : " + site.getName()));
        infoBox.getChildren().add(new Label("Περιοχή: " + site.getRegion()));
        infoBox.getChildren().add(new Label("Συντεταγμένες: " + site.getCoordinates()));

        // === ΣΩΣΤΟ building count ===
        // Φέρνουμε πραγματικά buildings από REST
        int buildingCount = buildingClient.getBuildingsBySite(siteId).size();
        infoBox.getChildren().add(new Label("Κτίρια: " + buildingCount));

        // Site δεν πρέπει να έχει QR (επιλογή)
        // Αν θέλεις QR για site, μπορούμε να το προσθέσουμε εδώ
    }


    private void loadBuildingMetadata(Long buildingId) {
        BuildingModel b = buildingClient.getBuilding(buildingId);

        infoBox.getChildren().add(new Label("Όνομα κτιρίου: " + b.getName()));
        infoBox.getChildren().add(new Label("Διεύθυνση: " + b.getAddress()));
        infoBox.getChildren().add(new Label("ID Μονάδας: " + b.getSiteId()));
    }

    private void loadDeviceMetadata(Long deviceId) {
        DeviceModel d = deviceClient.getDevice(deviceId);

        infoBox.getChildren().add(new Label("Όνομα συσκευής: " + d.getName()));
        infoBox.getChildren().add(new Label("Τύπος: " + d.getType()));
        infoBox.getChildren().add(new Label("Σειριακός αριθμός: " + d.getSerialNumber()));
        infoBox.getChildren().add(new Label("Ημερομηνία εγκατάστασης: " + d.getInstallationDate()));
        infoBox.getChildren().add(new Label("Firmware: " + d.getFirmwareVersion()));
        infoBox.getChildren().add(new Label("Κατάσταση: " + d.getStatus()));
        infoBox.getChildren().add(new Label("ID Κτίριο: " + d.getBuildingId()));
    }


    // -------------------------------
    //  QR HANDLING
    // -------------------------------

    private void loadQR(String path) {
        try {
            Image img = new Image(BASE_URL + path, true);
            qrImage.setImage(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void onExportQr() {
        try {
            if (qrImage.getImage() == null) return;

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Αποθήκευση QR Code");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Αρχεία", "*.png")
            );

            File file = chooser.showSaveDialog(null);
            if (file == null) return;

            BufferedImage buffered = SwingFXUtils.fromFXImage(qrImage.getImage(), null);
            ImageIO.write(buffered, "png", file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void onPrintQr() {
        try {
            if (qrImage.getImage() == null) return;

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(null)) {
                job.printPage(qrImage);
                job.endJob();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onScanQr() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dialogs/QRScannerDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("QR Scanner");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
