package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertSeverity;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.AlertServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @FXML private TableColumn<AlertModel, LocalDateTime> colDueDate;
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
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getStatus().getLabel()));

        alertsTable.setItems(data);

        addAlertButton.setOnAction(e -> showAddAlertDialog());

        alertsTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(AlertModel a, boolean empty) {
                super.updateItem(a, empty);

                if (a == null || empty) {
                    setStyle("");
                    return;
                }

                if (a.getSeverity() == AlertSeverity.CRITICAL) {
                    setStyle("-fx-background-color: rgba(244,67,54,0.30);"
                            + "-fx-font-weight: bold;");
                }
            }
        });

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
        Spinner<LocalTime> timeSpinner = new Spinner<>();
        SpinnerValueFactory<LocalTime> timeFactory =
                new SpinnerValueFactory<LocalTime>() {

                    {
                        setValue(LocalTime.now().withSecond(0).withNano(0));
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

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Ανοιχτό", "Επιλύθηκε","Ενημερωμένο");
        statusBox.setValue("Ανοιχτό");

        grid.add(new Label("Μήνυμα:"), 0, 0);
        grid.add(messageField, 1, 0);

        grid.add(new Label("Ημερομηνία:"), 0, 1);
        grid.add(dueDatePicker, 1, 1);
        grid.add(timeSpinner, 2, 1);

        grid.add(new Label("Κατάσταση:"), 0, 2);
        grid.add(statusBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {
                LocalDate date = dueDatePicker.getValue();
                LocalTime time = timeSpinner.getValue();

                if (date == null || time == null) {
                    return null;
                }
                AlertModel alert = new AlertModel();
                alert.setDeviceId(MainController.SelectionContext.selectedDeviceId);
                alert.setMessage(messageField.getText());
                alert.setDueDate(LocalDateTime.of(date, time));
                alert.setStatus(AlertStatus.valueOf(statusBox.getValue()));

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
