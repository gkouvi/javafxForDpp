package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertSeverity;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.AlertServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertStatus;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EditAlertController {

    public ComboBox<AlertSeverity> severityCombo;
    public ComboBox<AlertStatus> statusCombo;
    @FXML private TextField alertId;
    @FXML private TextField deviceId;
    @FXML private TextField message;
    @FXML private DatePicker dueDate;
    @FXML private Spinner<LocalTime> timeSpinner;
    @FXML private TextField status;

    private final AlertServiceClient alertServiceClient = new AlertServiceClient();

    public void loadAlert(AlertModel d) {
        alertId.setText(String.valueOf(d.getAlertId()));
        deviceId.setText(String.valueOf(d.getDeviceId()));
        message.setText(d.getMessage());
        dueDate.setValue(d.getDueDate().toLocalDate());
        statusCombo.setItems(
                FXCollections.observableArrayList(AlertStatus.values())
        );

        severityCombo.setItems(
                FXCollections.observableArrayList(AlertSeverity.values())
        );


        statusCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(AlertStatus item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getLabel());
            }
        });

        statusCombo.setButtonCell(
                statusCombo.getCellFactory().call(null)
        );

        severityCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(AlertSeverity item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getLabel());
            }
        });

        severityCombo.setButtonCell(
                severityCombo.getCellFactory().call(null)
        );


        // Προαιρετικό: default επιλογές
        statusCombo.setValue(d.getStatus());
        severityCombo.setValue(d.getSeverity());

        SpinnerValueFactory<LocalTime> timeFactory =
                new SpinnerValueFactory<LocalTime>() {

                    {
                        setValue(d.getDueDate().toLocalTime().withSecond(0).withNano(0));
                    }

                    @Override
                    public void decrement(int steps) {
                        setValue(getValue().minusMinutes(15 * steps));
                    }

                    @Override
                    public void increment(int steps) {
                        setValue(getValue().plusMinutes(15 * steps));
                    }
                };

        timeSpinner.setValueFactory(timeFactory);
        timeSpinner.setEditable(true);


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
            LocalDate date = dueDate.getValue();
            LocalTime time = timeSpinner.getValue();





            d.setDueDate( LocalDateTime.of(date, time));
            d.setSeverity(severityCombo.getValue());
            d.setStatus(statusCombo.getValue());

            AlertModel updated = alertServiceClient.updateAlert(d);

           ((Stage) alertId.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
