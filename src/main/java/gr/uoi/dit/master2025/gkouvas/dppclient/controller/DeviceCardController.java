package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.ls.LSOutput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

public class DeviceCardController {

    public Button createMaintenanceBtn;
    public Button addMaintenanceBtn;
    public Button uploadBtn;
    public Button downloadBtn;
    public TextArea materialsArea;
    public TextArea recyclingArea;
    public TextArea hazardousArea;
    public Label recyclabilityLabel;
    public Label weightLabel;
    public Label envScoreLabel;
    @FXML private Label deviceNameLabel;
    @FXML private Label deviceTypeLabel;
    @FXML private Label serialLabel;
    @FXML private Label statusLabel;
    @FXML private Label installationLabel;
    @FXML private Label firmwareLabel;
    @FXML private Label ipLabel;
    @FXML private Label nextMaintenanceLabel;

    @FXML private FlowPane intervalsFlow;

    @FXML
    private TableView<MaintenanceModel> maintenanceTable;
    @FXML private TableColumn<MaintenanceModel, String> mDateCol;
    @FXML private TableColumn<MaintenanceModel, String> mIntervalCol;
    @FXML private TableColumn<MaintenanceModel, String> mNotesCol;

    @FXML private TableView<AlertModel> alertsTable;
    @FXML private TableColumn<AlertModel, String> aTimeCol;
    @FXML private TableColumn<AlertModel, String> aTypeCol;
    @FXML private TableColumn<AlertModel, String> aMsgCol;

    @FXML private TableView<DocumentModel> documentsTable;
    @FXML private TableColumn<DocumentModel, String> dNameCol;
    @FXML private TableColumn<DocumentModel, String> dDateCol;

    @FXML private ImageView qrImage;

    private Long deviceId;
    private final EnvironmentalInfoServiceClient envClient = new EnvironmentalInfoServiceClient();


    private final DeviceServiceClient deviceClient = new DeviceServiceClient();
    private final MaintenanceServiceClient maintenanceClient = new MaintenanceServiceClient();
    private final AlertServiceClient alertClient = new AlertServiceClient();
    private final DocumentServiceClient documentClient = new DocumentServiceClient();


    /*@FXML
    public void initialize() {

        loadDevice();
        loadMaintenance();
        loadAlerts();
        loadDocuments();
        loadEnvironmentalInfo();

    }*/
    public void setDeviceId(Long id) {
        this.deviceId = id;

    }

    // -----------------------
    // ΣΥΣΚΕΥΗ ΦΟΡΤΩΣΗΣ
    // -----------------------
    private void loadDevice() {
        DeviceModel d = deviceClient.getDevice((long) deviceId);

        deviceNameLabel.setText(d.getName());
        deviceTypeLabel.setText(d.getType());
        serialLabel.setText(d.getSerialNumber());
        statusLabel.setText(d.getStatus());
        installationLabel.setText(String.valueOf(d.getInstallationDate()));
        firmwareLabel.setText(d.getFirmwareVersion());
        ipLabel.setText(d.getIpAddress());

        renderIntervals(d.getMaintenanceIntervals());
        nextMaintenanceLabel.setText(d.getNextMaintenanceDate().toString());

    }

   /* private void renderIntervals(List<MaintenanceInterval> intervals) {
        intervalsFlow.getChildren().clear();

        if (intervals == null) return;
        for (MaintenanceInterval interval : intervals) {

            Label chip = new Label();  // 👉 Πρέπει ΝΑ ΤΟ ΔΗΜΙΟΥΡΓΕΙΣ ΕΔΩ

            switch (interval) {
                case DAILY -> chip.setText("Ημερήσια");
                case MONTHLY -> chip.setText("Μηνιαία");
                case SEMI_ANNUAL -> chip.setText("Εξαμηνιαία");
                case ANNUAL -> chip.setText("Ετήσια");
            }

            chip.getStyleClass().add("chip");
            chip.getStyleClass().add("chip-" + interval.name().toLowerCase());

            intervalsFlow.getChildren().add(chip);
        }


       *//* for (MaintenanceInterval interval : intervals) {
            switch (interval.name()){
                case "DAILY":{
                    Label chip = new Label(); // ή interval.getName() αν έχεις custom field
                    chip.setText("Ημερήσια");
                    chip.getStyleClass().add("chip");              // βασικό στυλ
                    chip.getStyleClass().add("chip-" + interval.name().toLowerCase()); // χρώμα ανά interval

                    intervalsFlow.getChildren().add(chip);
                    break;
                }
                case "MONTHLY":{
                    Label chip = new Label(); // ή interval.getName() αν έχεις custom field
                    chip.setText("ΜΗΝΙΑΙΑ");
                    chip.getStyleClass().add("chip");              // βασικό στυλ
                    chip.getStyleClass().add("chip-" + interval.name().toLowerCase()); // χρώμα ανά interval

                    intervalsFlow.getChildren().add(chip);
                    break;
                }
                case "SEMI_ANNUAL":{
                    Label chip = new Label(); // ή interval.getName() αν έχεις custom field
                    chip.setText("Εξαμηνιαία");
                    chip.getStyleClass().add("chip");              // βασικό στυλ
                    chip.getStyleClass().add("chip-" + interval.name().toLowerCase()); // χρώμα ανά interval

                    intervalsFlow.getChildren().add(chip);
                    break;
                }
                case "ANNUAL":{
                    Label chip = new Label(); // ή interval.getName() αν έχεις custom field
                    chip.setText("Ετήσια");
                    chip.getStyleClass().add("chip");              // βασικό στυλ
                    chip.getStyleClass().add("chip-" + interval.name().toLowerCase()); // χρώμα ανά interval

                    intervalsFlow.getChildren().add(chip);
                    break;
                }
            }


            *//**//*chip.getStyleClass().add("chip");              // βασικό στυλ
            chip.getStyleClass().add("chip-" + interval.name().toLowerCase()); // χρώμα ανά interval

            intervalsFlow.getChildren().add(chip);*//**//*
        }*//*
    }*/
   private void renderIntervals(List<MaintenanceInterval> intervals) {
       intervalsFlow.getChildren().clear();
       if (intervals == null) return;

       DeviceModel d = deviceClient.getDevice(deviceId);
       LocalDate last = d.getLastMaintenanceDate();
       LocalDate today = LocalDate.now();

       for (MaintenanceInterval interval : intervals) {

           Label chip = new Label();

           // ---- LABEL TEXT ----
           switch (interval) {
               case DAILY -> chip.setText("Ημερήσια");
               case MONTHLY -> chip.setText("Μηνιαία");
               case SEMI_ANNUAL -> chip.setText("Εξαμηνιαία");
               case ANNUAL -> chip.setText("Ετήσια");
           }

           // ---- BASE CHIP STYLE ----
           chip.getStyleClass().add("chip");
           chip.getStyleClass().add("chip-" + interval.name().toLowerCase());

           // ---- ΥΠΟΛΟΓΙΣΜΟΣ NEXT DATE ----
           LocalDate next = switch (interval) {
               case DAILY -> last.plusDays(1);
               case MONTHLY -> last.plusMonths(1);
               case SEMI_ANNUAL -> last.plusMonths(6);
               case ANNUAL -> last.plusYears(1);
           };

           // ---- DANGER (κόκκινο) ----
           if (next.isBefore(today)) {
               chip.getStyleClass().add("chip-danger");
           }
           // ---- WARNING (πορτοκαλί) ----
           else if (!next.isAfter(today.plusDays(7))) {
               chip.getStyleClass().add("chip-warning");
           }

           intervalsFlow.getChildren().add(chip);
       }
   }



    // -----------------------
    // ΚΑΡΤΕΛΑ ΣΥΝΤΗΡΗΣΗΣ
    // -----------------------
    private void loadMaintenance() {

        mDateCol.setCellValueFactory(c -> {
            LocalDate d = c.getValue().getMaintenanceDate();
            return new SimpleStringProperty(d != null ? d.toString() : "—");
        });

        mIntervalCol.setCellValueFactory(c -> {
            MaintenanceInterval interval = c.getValue().getInterval();
            return new SimpleStringProperty(interval != null ? interval.toString() : "—");
        });

        mNotesCol.setCellValueFactory(c -> {
            String notes = c.getValue().getDescription();
            return new SimpleStringProperty(notes != null ? notes : "");
        });

        maintenanceTable.getItems().setAll(
                maintenanceClient.getMaintenanceByDevice((long) deviceId)
        );
    }


    // -----------------------
    // ΚΑΡΤΕΛΑ ΕΙΔΟΠΟΙΗΣΕΙΣ
    // -----------------------
    private void loadAlerts() {
        aTimeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDueDate().toString()));
        aTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        aMsgCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMessage()));

        alertsTable.getItems().setAll(alertClient.getAlertsForDevice((long) deviceId));
        System.out.println(alertClient.getAlertsForDevice((long) deviceId));
    }

    // -----------------------
    // ΚΑΡΤΕΛΑ ΕΓΓΡΑΦΑ
    // -----------------------
    private void loadDocuments() {
        try {
            List<DocumentModel> docs = documentClient.getDocumentsByDevice((long) deviceId);

            dNameCol.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().getFilename())
            );

            dDateCol.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getUploadedAt() == null
                                    ? "—"
                                    : c.getValue().getUploadedAt().toString()
                    )
            );

            documentsTable.getItems().setAll(docs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------
    // ΚΑΡΤΕΛΑ ΠΕΡΙΒΑΛΛΟΝ
    // -----------------------

    private void loadEnvironmentalInfo() {

        EnvironmentalInfoModel env = envClient.getByDevice(deviceId);

        if (env == null) {
            // Αν δεν υπάρχουν δεδομένα, εμφάνισε "-”
            materialsArea.setText("—");
            recyclingArea.setText("—");
            hazardousArea.setText("—");
            recyclabilityLabel.setText("—");
            weightLabel.setText("—");
            return;
        }

        materialsArea.setText(defaultIfNull(env.getMaterialsComposition(), "—"));
        recyclingArea.setText(defaultIfNull(env.getRecyclingInstructions(), "—"));
        hazardousArea.setText(defaultIfNull(env.getHazardousMaterials(), "—"));

        recyclabilityLabel.setText(
                env.getRecyclabilityPercentage() != null
                        ? env.getRecyclabilityPercentage() + "%"
                        : "—"
        );

        weightLabel.setText(
                env.getDeviceWeightKg() != null
                        ? env.getDeviceWeightKg() + " kg"
                        : "—"
        );
        int score = env.computeEnvironmentalScore();
        envScoreLabel.setText(score + "/100");

        envScoreLabel.getStyleClass().removeAll(
                "env-score-green", "env-score-yellow", "env-score-red"
        );

        if (score >= 70) envScoreLabel.getStyleClass().add("env-score-green");
        else if (score >= 40) envScoreLabel.getStyleClass().add("env-score-yellow");
        else envScoreLabel.getStyleClass().add("env-score-red");

        Tooltip tp = buildEnvironmentalTooltip(env, score);
        Tooltip.install(envScoreLabel, tp);


    }

    private String defaultIfNull(String v, String def) {
        return (v == null || v.isBlank()) ? def : v;
    }
    private Tooltip buildEnvironmentalTooltip(EnvironmentalInfoModel info, int score) {

        double recyclability = info.getRecyclabilityPercentage() != null
                ? info.getRecyclabilityPercentage()
                : 0;

        double weight = info.getDeviceWeightKg() != null
                ? info.getDeviceWeightKg()
                : 0;

        String hazards = info.getHazardousMaterials() != null
                ? info.getHazardousMaterials()
                : "—";

        // Recompute detailed subscores
        double weightScore =
                weight < 1 ? 100 :
                        weight < 3 ? 70 :
                                weight < 10 ? 40 :
                                        10;

        double hazardScore =
                hazards.isBlank() ? 100 :
                        hazards.toLowerCase().contains("pb") ||
                                hazards.toLowerCase().contains("hg") ||
                                hazards.toLowerCase().contains("cr6")
                                ? 20 : 60;

        String text =
                "♻ Ανακυκλωσιμότητα: " + recyclability + "%\n" +
                        "⚖ Βάρος: " + weight + " kg → score: " + (int)weightScore + "\n" +
                        "☣ Επικίνδυνα υλικά: " + hazards + " → score: " + (int)hazardScore + "\n\n" +
                        "📊 Τελικό περιβαλλοντικό σκορ: " + score + "/100";

        Tooltip tp = new Tooltip(text);
        tp.setStyle("-fx-font-size: 14px; -fx-font-weight: normal;");

        return tp;
    }





    public void loadDevice(Long deviceId) {
        this.deviceId = deviceId;
        DeviceModel d = deviceClient.getDevice((long) deviceId);

        System.out.println("loadDevice " + deviceId);

        deviceNameLabel.setText(d.getName());
        deviceTypeLabel.setText(d.getType());
        serialLabel.setText(d.getSerialNumber());
        statusLabel.setText(d.getStatus());
        installationLabel.setText(String.valueOf(d.getInstallationDate()));
        firmwareLabel.setText(d.getFirmwareVersion());
        ipLabel.setText(d.getIpAddress());

        renderIntervals(d.getMaintenanceIntervals());LocalDate next = computeNextMaintenance(d);
        if (next != null) nextMaintenanceLabel.setText(next.toString());
        else nextMaintenanceLabel.setText("—");

        loadQr(d);
        loadMaintenance();
        loadAlerts();
        loadDocuments();
        loadEnvironmentalInfo();
    }
    private LocalDate computeNextMaintenance(DeviceModel d) {

        if (d.getLastMaintenanceDate() == null || d.getMaintenanceIntervals() == null) {
            return null;
        }

        LocalDate last = d.getLastMaintenanceDate();

        return d.getMaintenanceIntervals().stream()
                .map(i -> switch (i) {
                    case DAILY -> last.plusDays(1);
                    case MONTHLY -> last.plusMonths(1);
                    case SEMI_ANNUAL -> last.plusMonths(6);
                    case ANNUAL -> last.plusYears(1);
                })
                .min(LocalDate::compareTo)
                .orElse(null);
    }
    private void loadQr(DeviceModel d) {
        try {
            if (d.getQrBase64() == null) return;

            byte[] bytes = Base64.getDecoder().decode(d.getQrBase64());
            BufferedImage buffered = ImageIO.read(new ByteArrayInputStream(bytes));

            qrImage.setImage(SwingFXUtils.toFXImage(buffered, null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===========================================================
// ΠΡΟΣΘΗΚΗ ΣΥΝΤΗΡΗΣΗΣ (ανοίγει παράθυρο διαλόγου)
// ===========================================================
    @FXML
    private void onAddMaintenance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MaintenanceCreateDialog.fxml"));
            Parent root = loader.load();

            MaintenanceCreateDialogController controller = loader.getController();
            controller.setDeviceId((long) deviceId); // pass device ID to dialog

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Προσθήκη συντήρησης");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // refresh table after closing dialog
            loadMaintenance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ===========================================================
// ΔΗΜΙΟΥΡΓΙΑ ΣΥΝΤΗΡΗΣΗΣ (άμεση – αυτόματη χρήση πλησιέστερου διαστήματος)
// ===========================================================
    @FXML

    private void onCreateMaintenance() {

        // ===========================================================
        // LOAD DEVICE SAFELY
        // ===========================================================
        DeviceModel d = deviceClient.getDevice((long) deviceId);


        if (d == null) {
            showError("Device Error", "Unable to load device from server.");
            return;
        }

        if (d.getMaintenanceIntervals() == null || d.getMaintenanceIntervals().isEmpty()) {
            showError("Χωρίς διαστήματα", "Αυτή η συσκευή δεν έχει διαστήματα συντήρησης..");
            return;
        }

        // ===========================================================
        // COMPUTE CLOSEST INTERVAL
        // ===========================================================
        LocalDate today = LocalDate.now();
        LocalDate next = null;
        MaintenanceInterval closest = null;

        for (MaintenanceInterval i : d.getMaintenanceIntervals()) {

            LocalDate candidate = switch (i) {
                case DAILY -> today.plusDays(1);
                case MONTHLY -> today.plusMonths(1);
                case SEMI_ANNUAL -> today.plusMonths(6);
                case ANNUAL -> today.plusYears(1);
            };

            if (next == null || candidate.isBefore(next)) {
                next = candidate;
                closest = i;
            }
        }

        if (closest == null) {
            showError("Σφάλμα υπολογισμού", "Δεν ήταν δυνατό να προσδιοριστεί το διάστημα συντήρησης.");
            return;
        }

        // ===========================================================
        // CREATE MAINTENANCE MODEL
        // ===========================================================
        MaintenanceModel log = new MaintenanceModel();
        log.setDeviceId((long) deviceId);
        log.setMaintenanceDate(today);
        log.setInterval(closest);
        log.setDescription("Συντήρηση αυτοκινήτου που δημιουργήθηκε από την κάρτα συσκευής");

        MaintenanceModel ok = maintenanceClient.createMaintenance(log);

        if (ok==null) {
            showError("Server Error", "Αποτυχία δημιουργίας συντήρησης.");
            return;
        }

        // ===========================================================
        // REFRESH UI
        // ===========================================================
        loadMaintenance();
        showInfo("Δημιουργία συντήρησης", "Η συντήρηση του αυτοκινήτου δημιουργήθηκε με επιτυχία.");
    }

    // ===========================================================
// Ανεβάστε έγγραφο
// ===========================================================
    @FXML
    private void onUploadDocument() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Ανεβάστε έγγραφο");
            File file = fc.showOpenDialog(uploadBtn.getScene().getWindow());

            if (file == null) return;

            documentClient.uploadDocument((long) deviceId, file);

            loadDocuments();
            showInfo("Ανεβασμένο", "Το έγγραφο μεταφορτώθηκε με επιτυχία.");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Σφάλμα μεταφόρτωσης", e.getMessage());
        }
    }


    // ===========================================================
// Λήψη εγγράφου
// ===========================================================
    @FXML
    private void onDownloadDocument() {
        try {
            DocumentModel selected = documentsTable.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError("Καμία επιλογή", "Παρακαλώ επιλέξτε πρώτα ένα έγγραφο.");
                return;
            }

            // Download bytes from backend
            byte[] bytes = documentClient.downloadDocument(selected.getId());

            FileChooser fc = new FileChooser();
            fc.setInitialFileName(selected.getFilename());

            File saveFile = fc.showSaveDialog(downloadBtn.getScene().getWindow());
            if (saveFile == null) return;

            Files.write(saveFile.toPath(), bytes);

            showInfo("Κατέβηκε", "Το έγγραφο αποθηκεύτηκε με επιτυχία.");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Σφάλμα λήψης", e.getMessage());
        }
    }



    // ===========================================================
// ΠΡΟΒΟΛΕΣ ΧΡΗΣΙΜΟΤΗΤΑΣ
// ===========================================================
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }


    public void onEditEnvironmental(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EnvironmentalInfoDialog.fxml"));
            Parent root = loader.load();

            EnvironmentalInfoDialogController ctrl = loader.getController();
            ctrl.setDeviceId(deviceId);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Περιβαλλοντικές Πληροφορίες");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadEnvironmentalInfo(); // refresh tab

        } catch (Exception e) {
            e.printStackTrace();
            showError("Σφάλμα", "Αποτυχία ανοίγματος παραθύρου.");
        }
    }
}

