package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.AlertServiceClient;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditAlertController {

    @FXML private TextField alertId;
    @FXML private TextField deviceId;
    @FXML private TextField message;
    @FXML private DatePicker dueDate;
    @FXML private TextField status;

    private final AlertServiceClient alertServiceClient = new AlertServiceClient();

    public void loadAlert(AlertModel d) {
        alertId.setText(String.valueOf(d.getAlertId()));
        deviceId.setText(String.valueOf(d.getDeviceId()));
        message.setText(d.getMessage());
        dueDate.setValue(d.getDueDate());
        status.setText(d.getStatus());
    }

    @FXML
    public void onCancel() {
        ((Stage) alertId.getScene().getWindow()).close();
    }

    @FXML
    public void onSave() {
        try {
            AlertModel d = new AlertModel();
            d.setAlertId(Long.parseLong(alertId.getText()));
            d.setDeviceId(Long.parseLong(deviceId.getText()));
            d.setMessage(message.getText());
            d.setDueDate(dueDate.getValue());
            d.setStatus(status.getText());

            AlertModel updated = alertServiceClient.updateAlert(d);

           ((Stage) alertId.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
