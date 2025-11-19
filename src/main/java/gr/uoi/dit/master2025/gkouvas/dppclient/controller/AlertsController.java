package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.AlertServiceClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Alerts tab.
 * Displays alerts for the selected device and allows adding new alerts.
 *
 * IMPORTANT:
 * initialize() runs once (on FXML load)
 * refresh(deviceId) runs EVERY time the user selects a different device
 */
public class AlertsController {

    @FXML private TableView<AlertModel> alertsTable;

    @FXML private TableColumn<AlertModel, Long> colId;
    @FXML private TableColumn<AlertModel, String> colMessage;
    @FXML private TableColumn<AlertModel, LocalDate> colDueDate;
    @FXML private TableColumn<AlertModel, String> colStatus;

    @FXML private Button addAlertButton;

    private final AlertServiceClient client = new AlertServiceClient();
    private final ObservableList<AlertModel> data = FXCollections.observableArrayList();


    /**
     * Runs ONLY ONCE when FXML is loaded.
     */
    @FXML
    public void initialize() {

        // Bind table columns
        colId.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getAlertId()));

        colMessage.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getMessage()));

        colDueDate.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getDueDate()));

        colStatus.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getStatus()));

        alertsTable.setItems(data);

        addAlertButton.setOnAction(e -> showAddAlertDialog());
    }


    /**
     * Called by MainController EVERY TIME a different device is selected.
     */
    public void refresh(Long deviceId) {
        data.clear();
        if (deviceId != null) {
            loadAlerts(deviceId);
        }
    }


    /**
     * Load alerts from backend
     */
    private void loadAlerts(Long deviceId) {
        List<AlertModel> alerts = client.getAlertsForDevice(deviceId);
        data.setAll(alerts);
    }


    /**
     * Dialog for adding a new alert
     */
    private void showAddAlertDialog() {

        Dialog<AlertModel> dialog = new Dialog<>();
        dialog.setTitle("Add Alert");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        TextField messageField = new TextField();
        DatePicker dueDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active", "Resolved");
        statusBox.setValue("Active");

        grid.add(new Label("Message:"), 0, 0);
        grid.add(messageField, 1, 0);

        grid.add(new Label("Due Date:"), 0, 1);
        grid.add(dueDatePicker, 1, 1);

        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {

                AlertModel alert = new AlertModel();
                alert.setDeviceId(MainController.SelectionContext.selectedDeviceId);
                alert.setMessage(messageField.getText());
                alert.setDueDate(dueDatePicker.getValue());
                alert.setStatus(statusBox.getValue());

                return alert;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(alert -> {
            AlertModel saved = client.createAlert(alert);
            if (saved != null) {
                data.add(saved);
            }
        });
    }
}
