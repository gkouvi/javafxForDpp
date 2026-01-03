package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class QRScannerDialogController {

    public TextField usbScanner;
    @FXML private ImageView cameraView;


    private Webcam webcam;
    private volatile boolean running = true;
    private Thread cameraThread;

    @FXML
    public void initialize() {
        usbScanner.setFocusTraversable(true);
        Platform.runLater(() -> usbScanner.requestFocus());
        startScanner();
    }

    private void startScanner() {

        cameraThread = new Thread(() -> {

            try {
                webcam = Webcam.getDefault();
                if (webcam == null) return;

                // FIX: ensure webcam is CLOSED before setting resolution
                if (webcam.isOpen()) {
                    webcam.close();
                }

                webcam.setViewSize(new Dimension(640, 480));
                webcam.open();

                while (running) {

                    BufferedImage image = webcam.getImage();
                    if (image == null) continue;

                    Platform.runLater(() ->
                            cameraView.setImage(SwingFXUtils.toFXImage(image, null))
                    );

                    String result = decodeQR(image);

                    if (result != null) {
                        running = false;

                        Platform.runLater(() -> {
                            MainController.instance.handleScannedQR(result);

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("QR Βρέθηκε");
                            alert.setHeaderText("Σαρωμένο:");
                            alert.setContentText(result);
                            alert.show();

                            closeWindow();
                        });

                        break;
                    }

                    Thread.sleep(50);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        cameraThread.setDaemon(true);
        cameraThread.start();
    }


    private String decodeQR(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);

            return result.getText();

        } catch (Exception e) {
            return null;
        }
    }
    @FXML
    private void onUsbScan(KeyEvent event) {

        if (event.getCode().toString().equals("ENTER")) {

            String qr = usbScanner.getText().trim();
            usbScanner.clear();


            if (!qr.isEmpty()) {

                // Σταματάμε την κάμερα
                running = false;


                handleScannedQR(qr);

                // Optional pop-up
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("QR Scan");
                alert.setHeaderText("Scanned (USB):");
                alert.setContentText(qr);
                alert.show();

                closeWindow();
            }
        }
    }
    public void handleScannedQR(String code) {


        if (code == null || code.isEmpty()) return;

        // ---- NEW FORMAT ----
        if (code.startsWith("DPP://device/")) {
            long id = Long.parseLong(code.substring("DPP://device/".length()));

            openDeviceCard(id);
            return;
        }

        // ---- OLD FORMAT (URL FORMAT) ----
        if (code.startsWith("http://localhost:8080/devices/qr/")) {
            long id = Long.parseLong(code.substring("http://localhost:8080/devices/qr/".length()));

            openDeviceCard(id);
            return;
        }

        // ---- ΚΤΙΡΙΟ ----
        if (code.startsWith("DPP://building/")) {
            long id = Long.parseLong(code.substring("DPP://building/".length()));

            return;
        }

        // ---- SITE ----
        if (code.startsWith("DPP://site/")) {
            long id = Long.parseLong(code.substring("DPP://site/".length()));

            return;
        }
    }



    @FXML
    public void onClose() {
        running = false;
        closeWindow();
    }

    private void closeWindow() {
        try {
            if (webcam != null && webcam.isOpen()) {
                webcam.close();
                cameraView. setImage(null);
            }

        } catch (Exception ignored) {
        }
        Landing.instance.loadDashboard();
       /* Stage stage = (Stage) cameraView.getScene().getWindow();
        stage.close();*/
    }
    public void openDeviceCard(Long deviceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deviceCard.fxml"));
            Parent root = loader.load();

            DeviceCardController controller = loader.getController();
            controller.loadDevice(deviceId);

            Stage stage = new Stage();
            stage.setTitle("Λεπτομέρειες συσκευής");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
