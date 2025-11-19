package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

public class DeviceCardController {

    @FXML private Label nameLabel;
    @FXML private Label typeLabel;
    @FXML private Label serialLabel;
    @FXML private Label firmwareLabel;
    @FXML private Label statusLabel;
    @FXML private Label installedLabel;
    @FXML private Label buildingLabel;
    @FXML private ImageView qrImage;

    private final DeviceServiceClient deviceClient = new DeviceServiceClient();

    /**
     * Load the device data from backend and update UI.
     */
    public void loadDevice(Long deviceId) {
        DeviceModel device = deviceClient.getDevice(deviceId);

        if (device == null) {
            nameLabel.setText("Device not found");
            return;
        }

        nameLabel.setText(device.getName());
        typeLabel.setText(device.getType());
        serialLabel.setText(device.getSerialNumber());
        firmwareLabel.setText(device.getFirmwareVersion());
        statusLabel.setText(device.getStatus());
        installedLabel.setText(String.valueOf(device.getInstallationDate()));
        buildingLabel.setText(String.valueOf(device.getBuildingId()));

        loadQr(device);
    }

    /**
     * Converts base64 QR into JavaFX image
     */
    private void loadQr(DeviceModel d) {
        try {
            if (d.getQrBase64() == null) return;

            byte[] bytes = Base64.getDecoder().decode(d.getQrBase64());
            BufferedImage buffered = ImageIO.read(new ByteArrayInputStream(bytes));

            qrImage.setImage(SwingFXUtils.toFXImage(buffered, null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onClose() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}
