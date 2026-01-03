/*
package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceInterval;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.QRUtil;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class CreateDeviceController {

    public TextField ipAddressField;
    public ComboBox<String> comboForBuildings;
    public CheckBox hasIPCheck;
    public CheckBox dailyCheck;
    public CheckBox monthlyCheck;
    public CheckBox sixMonthCheck;
    public CheckBox yearlyCheck;

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
            device.setIpAddress(ipAddressField.getText());
            switch (comboForBuildings.getValue()) {
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
*/
package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.EnvironmentalInfoServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.QRUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CreateDeviceController {

    @FXML private TextField bimElementIdField;
    @FXML private ComboBox<String> bimDisciplineCombo;
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField serialField;
    @FXML private TextField firmwareField;
    @FXML private DatePicker dateField;
    @FXML private ComboBox<DeviceStatus> statusField;
    @FXML private TextField ipAddressField;
   // @FXML private TextField buildingIdField;

    @FXML private ComboBox<BuildingModel> comboForBuildings;

    @FXML private CheckBox dailyCheck;
    @FXML private CheckBox monthlyCheck;
    @FXML private CheckBox sixMonthCheck;
    @FXML private CheckBox yearlyCheck;


    private final DeviceServiceClient deviceClient = new DeviceServiceClient();
    private Long buildingId;
    private final SiteServiceClient siteClient = new SiteServiceClient();
    private final BuildingServiceClient buildingClient = new BuildingServiceClient();
    private final ObservableList<BuildingModel> modelList = FXCollections.observableArrayList();
    private EnvironmentalInfoModel tempEnvInfo;
    private final EnvironmentalInfoServiceClient envClient = new EnvironmentalInfoServiceClient();


    private Long selectedBuildingId = null;

    @FXML
    public void initialize() {

        // Load Buildings from MainController (same system as Edit Device)
        comboForBuildings.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(BuildingModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getSite() + " / " + item.getName());
                }
            }
        });

        comboForBuildings.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(BuildingModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getSite() + " / " + item.getName());
                }
            }
        });

        for (var site : siteClient.getAllSites()) {
            for (var building : buildingClient.getAllBuildings()) {

                modelList.add(building);
            }

            // Fill ComboBox with all buildings

            comboForBuildings.getItems().addAll(modelList);


            // Selection handler
            comboForBuildings.setOnAction(e -> {
                BuildingModel b = comboForBuildings.getValue();
                if (b != null) selectedBuildingId = b.getId();
            });
        }
        bimDisciplineCombo.getItems().setAll(
                "HVAC", "Ηλεκτρικά", "Ασφάλεια", "Πυρασφάλεια", "Υδραυλικά", "IT/Δίκτυο", "Δομικά", "Άλλα"
        );
        statusField.setItems(FXCollections.observableArrayList(DeviceStatus.values()));
    }

    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    public void onCreate() {
        try {

            // 1. Συλλογή διαστημάτων συντήρησης
            List<MaintenanceInterval> intervals = new ArrayList<>();

            if (dailyCheck.isSelected()) intervals.add(MaintenanceInterval.DAILY);
            if (monthlyCheck.isSelected()) intervals.add(MaintenanceInterval.MONTHLY);
            if (sixMonthCheck.isSelected()) intervals.add(MaintenanceInterval.SEMI_ANNUAL);
            if (yearlyCheck.isSelected()) intervals.add(MaintenanceInterval.ANNUAL);

            // 2. Construct device modeΚατασκευή μοντέλου συσκευήςl
            DeviceModel device = new DeviceModel();
            device.setName(nameField.getText());
            device.setType(typeField.getText());
            device.setSerialNumber(serialField.getText());
            device.setFirmwareVersion(firmwareField.getText());
            device.setInstallationDate(dateField.getValue());
            device.setStatus(statusField.getValue().name());
            device.setIpAddress(ipAddressField.getText());
            device.setOffline(false);

            device.setBuildingId(selectedBuildingId);
            device.setMaintenanceIntervals(intervals);

            device.setBimElementId(bimElementIdField.getText());
            device.setBimDiscipline(bimDisciplineCombo.getValue());
            // 3. Δημιουργία συσκευής και περιβάλλον
            DeviceModel created = deviceClient.createDevice(device);
            Long newId = created.getDeviceId();
            if (tempEnvInfo != null) {
                tempEnvInfo.setDeviceId(created.getDeviceId());
                envClient.saveEnvironmentalInfo(tempEnvInfo);
            }

            // 4. Δημιουργία QR
            String payload = "DPP://device/" + newId + "?v=1";
            if (payload == null || !payload.startsWith("DPP://")) {
                throw new IllegalArgumentException("Invalid DPP QR");
            }

            // DPP://device/42
            String[] parts = payload.replace("DPP://", "").split("/");

            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid DPP format");
            }
            BufferedImage qr = QRUtil.generateQRCode(payload, 300);

            // 5. Ανεβάστε QR
            deviceClient.uploadDeviceQr(newId, qr);

            // 6. Ανανέωση UI
            if (selectedBuildingId != null) {
                MainController.instance.refreshDevicesForBuilding(selectedBuildingId);
            }

            // 7. Close window
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBuildingId(Long id) {
        this.buildingId = id;


    }


    @FXML
    private void onEditEnvironmental() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/EnvironmentalInfoDialog.fxml"));

            Parent root = loader.load();
            EnvironmentalInfoDialogController ctrl = loader.getController();

            // Φόρτωση υπάρχοντων τιμών
            if (tempEnvInfo != null) {
                ctrl.setInfo(tempEnvInfo);
            }

            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Περιβαλλοντικές πληροφορίες");
            st.setScene(new Scene(root));
            st.showAndWait();

            // Αν ο χρήστης ΠΑΤΗΣΕ CANCEL → ΜΗΝ κάνεις overwrite το tempInfo
            if (!ctrl.isSaved()) {
                return;
            }

            // Αν πατήθηκε Save → κράτα τις τιμές
            tempEnvInfo = ctrl.getInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
