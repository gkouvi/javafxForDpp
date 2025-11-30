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
 * Εμφανίζει ειδοποιήσεις για την επιλεγμένη συσκευή και επιτρέπει την προσθήκη νέων ειδοποιήσεων.
 *
 * IMPORTANT:
 * initialize() εκτελείται μία φορά (on FXML load)
 * refresh(deviceId) εκτελείται ΚΑΘΕ φορά που ο χρήστης επιλέγει μια διαφορετική συσκευή
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
     * Εκτελείται ΜΟΝΟ ΜΙΑ ΦΟΡΑ όταν φορτώνεται το FXML.
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
     * Καλείται από το MainController ΚΑΘΕ ΦΟΡΑ που επιλέγεται μια διαφορετική συσκευή.
     */
    public void refresh(Long deviceId) {
        data.clear();
        if (deviceId != null) {
            loadAlerts(deviceId);
        }
    }


    /**
     * Φόρτωση ειδοποιήσεων από backend
     */
    private void loadAlerts(Long deviceId) {
        List<AlertModel> alerts = client.getAlertsForDevice(deviceId);
        data.setAll(alerts);
    }


    /**
     * Διάλογος για την προσθήκη νέας ειδοποίησης
     */
    private void showAddAlertDialog() {

        Dialog<AlertModel> dialog = new Dialog<>();
        dialog.setTitle("Προσθήκη ειδοποίησης");

        ButtonType saveBtn = new ButtonType("Αποθήκευση", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        TextField messageField = new TextField();
        DatePicker dueDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Ενεργός", "Επιλύθηκε");
        statusBox.setValue("Ενεργός");

        grid.add(new Label("Μήνυμα:"), 0, 0);
        grid.add(messageField, 1, 0);

        grid.add(new Label("Ημερομηνία:"), 0, 1);
        grid.add(dueDatePicker, 1, 1);

        grid.add(new Label("Κατάσταση:"), 0, 2);
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
