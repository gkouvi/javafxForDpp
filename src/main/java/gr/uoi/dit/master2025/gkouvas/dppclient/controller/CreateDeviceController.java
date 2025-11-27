package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceInterval;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.QRUtil;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.MultipartUtil;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class CreateDeviceController {

    public TextField ipAddress;
    public ComboBox<String> maintenanceCombo;
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField serialField;
    @FXML private TextField firmwareField;
    @FXML private DatePicker dateField;
    @FXML private TextField statusField;
    @FXML private TextField buildingIdField;

    private final DeviceServiceClient deviceClient = new DeviceServiceClient();

    private Long buildingId;

    public void setBuildingId(Long id) {
        this.buildingId = id;

        if (id == null) {
            buildingIdField.setText("Δεν έχει επιλεγεί κτίριο");
        } else {
            buildingIdField.setText(id.toString());
        }
    }


    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    public void onCreate() {
        try {
            // 1. CREATE DEVICE
            DeviceModel device = new DeviceModel();
            device.setName(nameField.getText());
            device.setType(typeField.getText());
            device.setSerialNumber(serialField.getText());
            device.setFirmwareVersion(firmwareField.getText());
            device.setInstallationDate(dateField.getValue());
            device.setStatus(statusField.getText());
            device.setBuildingId(buildingId);
            device.setOffline(false);
            device.setIpAddress(ipAddress.getText());
            switch (maintenanceCombo.getValue()) {
                case "Καθημερινά": device.setMaintenanceInterval(MaintenanceInterval.DAILY); break;
                case "Μηνιαία" :  device.setMaintenanceInterval(MaintenanceInterval.MONTHLY); break;
                case "Εξαμηνιαία" :device.setMaintenanceInterval(MaintenanceInterval.SEMI_ANNUAL); break;
               // case "Ετήσια" :device.setMaintenanceInterval(MaintenanceInterval.ANNUAL); break;
                default:device.setMaintenanceInterval(MaintenanceInterval.ANNUAL); break;
            }
            //device.setMaintenanceInterval(MaintenanceInterval.valueOf(maintenanceCombo.getValue()));

            DeviceModel created = deviceClient.createDevice(device);

            Long newId = created.getDeviceId();

            // 2. GENERATE QR IN CLIENT
            String payload = "DPP://device/" + newId;
            BufferedImage qr = QRUtil.generateQRCode(payload, 300);


            // 3. UPLOAD QR TO BACKEND
            deviceClient.uploadDeviceQr(newId, qr);

            // 4. REFRESH TREE IN MAIN
            MainController.instance.refreshDevicesForBuilding(buildingId);

            // 5. CLOSE WINDOW
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
