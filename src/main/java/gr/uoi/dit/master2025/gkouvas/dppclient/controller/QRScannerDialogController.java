package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class QRScannerDialogController {

    @FXML private ImageView cameraView;

    private Webcam webcam;
    private volatile boolean running = true;
    private Thread cameraThread;

    @FXML
    public void initialize() {
        startScanner();
    }

    private void startScanner() {

        cameraThread = new Thread(() -> {

            try {
                // Open webcam
                webcam = Webcam.getDefault();
                if (webcam == null) return;

                webcam.setViewSize(new Dimension(640, 480));
                webcam.open();

                while (running) {

                    BufferedImage image = webcam.getImage();
                    if (image == null) continue;

                    // Show webcam in UI
                    Platform.runLater(() ->
                            cameraView.setImage(SwingFXUtils.toFXImage(image, null))
                    );

                    // Try QR decode
                    String result = decodeQR(image);

                    if (result != null) {
                        running = false;

                        Platform.runLater(() -> {

                            // 🚀 Στέλνουμε το αποτέλεσμα στο MainController
                            MainController.instance.handleScannedQR(result);

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("QR Found");
                            alert.setHeaderText("Scanned:");
                            alert.setContentText(result);
                            alert.show();

                            closeWindow();
                        });

                        break;
                    }

                    Thread.sleep(50); // Smooth capture
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
    public void onClose() {
        running = false;
        closeWindow();
    }

    private void closeWindow() {
        try {
            if (webcam != null && webcam.isOpen()) {
                webcam.close();
            }
        } catch (Exception ignored) {
        }

        ((Stage) cameraView.getScene().getWindow()).close();
    }
}
