package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceInterval;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.MaintenanceServiceClient;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MaintenanceCreateDialogController {

    // --- FXML Fields ---
    @FXML private Label deviceLabel;
    @FXML private ComboBox<MaintenanceInterval> intervalCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField technicianField;
    @FXML private TextArea descriptionField;

    private Long deviceId = null;
    private final MaintenanceServiceClient maintenanceClient = new MaintenanceServiceClient();

    // ⬇ called from Dashboard when right-clicking → "Δημιουργία Συντήρησης Τώρα"
    public void setDeviceId(Long id) {
        this.deviceId = id;
        deviceLabel.setText("Συσκευή #" + id);
    }

    // ⬇ Optional: prefill interval from Upcoming Maintenance
    public void prefillInterval(MaintenanceInterval interval) {
        intervalCombo.setValue(interval);
    }

    // ⬇ Optional: prefill date (usually LocalDate.now())
    public void prefillDate(LocalDate date) {
        datePicker.setValue(date);
    }

    @FXML
    public void initialize() {

        // Fill combo with all intervals
        intervalCombo.getItems().setAll(
                MaintenanceInterval.DAILY,
                MaintenanceInterval.MONTHLY,
                MaintenanceInterval.SEMI_ANNUAL,
                MaintenanceInterval.ANNUAL
        );

        // Default date = today
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    public void onCancel() {
        ((Stage) deviceLabel.getScene().getWindow()).close();
    }

    @FXML
    public void onCreate() {

        if (deviceId == null) {
            showError("Σφάλμα", "Δεν ορίστηκε συσκευή.");
            return;
        }

        MaintenanceInterval selectedInterval = intervalCombo.getValue();
        if (selectedInterval == null) {
            showError("Σφάλμα", "Επιλέξτε διάστημα συντήρησης.");
            return;
        }

        LocalDate date = datePicker.getValue();
        if (date == null) {
            showError("Σφάλμα", "Ορίστε ημερομηνία.");
            return;
        }

        String tech = technicianField.getText();
        String desc = descriptionField.getText();

        // --- Build model ---
        MaintenanceModel log = new MaintenanceModel();
        log.setDeviceId(deviceId);
        log.setMaintenanceDate(date);
        log.setDescription(desc);
        log.setTechnician(tech);
        log.setInterval(selectedInterval);

        try {
            maintenanceClient.createMaintenance(log);

            // refresh if Dashboard is open
            if (MainController.instance != null) {
                MainController.instance.refreshSites();
            }

            ((Stage) deviceLabel.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Σφάλμα", "Αποτυχία δημιουργίας συντήρησης:\n" + e.getMessage());
        }
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
