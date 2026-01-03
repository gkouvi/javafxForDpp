package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceInterval;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceStatus;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.MaintenanceServiceClient;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

/*
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
*/
public class MaintenanceCreateDialogController {

    @FXML private DatePicker datePicker;
    @FXML private TextArea descArea;
    @FXML private TextField techField;
    @FXML private ComboBox<MaintenanceInterval> intervalCombo;
   // @FXML private ChoiceBox<MaintenanceInterval> intervalChoice;
    @FXML private CheckBox completedNowCheck;

    private final MaintenanceServiceClient maintenanceClient = new MaintenanceServiceClient();

    private Long deviceId;    // optional
    private Long buildingId;  // optional

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    @FXML
    private void initialize() {
        intervalCombo.getItems().setAll(MaintenanceInterval.values());
    }

    @FXML
    private void onSave() {
        try {
            LocalDate date = datePicker.getValue();
            String desc = descArea.getText();
            String technician = techField.getText();
            MaintenanceInterval interval = intervalCombo.getValue();

            if (date == null) {
                showError("Ημερομηνία", "Παρακαλώ δώστε ημερομηνία.");
                return;
            }

            MaintenanceModel m = new MaintenanceModel();
            m.setDescription(desc);
            m.setTechnician(technician);
            m.setInterval(interval);

            if (deviceId != null) m.setDeviceId(deviceId);
            if (buildingId != null) m.setBuildingId(buildingId);

            if (completedNowCheck.isSelected()) {
                // Ολοκληρώθηκε σήμερα
                m.setPerformedDate(LocalDate.now());
                m.setMaintenanceDate(LocalDate.now());
                m.setStatus(MaintenanceStatus.COMPLETED);
            } else {
                // Προγραμματισμένη συντήρηση
                m.setPlannedDate(date);
                m.setMaintenanceDate(date);
                m.setStatus(MaintenanceStatus.PENDING);
            }

            MaintenanceModel saved = maintenanceClient.createMaintenance(m);
            if (saved == null) {
                showError("Σφάλμα", "Η αποθήκευση απέτυχε.");
                return;
            }

            close();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Σφάλμα", "Λάθος δεδομένα: " + ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        ((Stage) datePicker.getScene().getWindow()).close();
    }

    private void showError(String t, String m) {
        Alert a = new Alert(Alert.AlertType.ERROR, m, ButtonType.OK);
        a.setTitle(t);
        a.showAndWait();
    }
}
