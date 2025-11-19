package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditDeviceController {

    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField serialField;
    @FXML private TextField firmwareField;
    @FXML private DatePicker dateField;
    @FXML private TextField statusField;

    private final DeviceServiceClient deviceClient = new DeviceServiceClient();
    private Long deviceId;

    public void loadDevice(DeviceModel d) {
        this.deviceId = d.getDeviceId();

        nameField.setText(d.getName());
        typeField.setText(d.getType());
        serialField.setText(d.getSerialNumber());
        firmwareField.setText(d.getFirmwareVersion());
        dateField.setValue(d.getInstallationDate());
        statusField.setText(d.getStatus());
    }

    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    public void onSave() {
        try {
            DeviceModel d = new DeviceModel();
            d.setDeviceId(deviceId);
            d.setName(nameField.getText());
            d.setType(typeField.getText());
            d.setSerialNumber(serialField.getText());
            d.setFirmwareVersion(firmwareField.getText());
            d.setInstallationDate(dateField.getValue());
            d.setStatus(statusField.getText());

            deviceClient.updateDevice(deviceId, d);

            MainController.instance.refreshDevicesForBuilding(
                    MainController.SelectionContext.selectedBuildingId);

            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

