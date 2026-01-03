package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DocumentModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DocumentServiceClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Files;

/**
 * Ελεγκτής για την καρτέλα Έγγραφα.
 *  Διαχειρίζεται την εμφάνιση, τη μεταφόρτωση και τη λήψη εγγράφων.
 *
 * ΣΗΜΕΙΩΣΗ:
 *  * - η εντολή initialize() εκτελείται μία φορά (ρύθμιση UI)
 *  * - η εντολή refresh(deviceId) εκτελείται ΚΑΘΕ φορά που ο χρήστης επιλέγει άλλη συσκευή
 */
public class DocumentsController {

    @FXML private Button btnUpload;
    @FXML private Button btnDownload;

    @FXML private TableView<DocumentModel> documentsTable;

    @FXML private TableColumn<DocumentModel, Long> colId;
    @FXML private TableColumn<DocumentModel, String> colFilename;
    @FXML private TableColumn<DocumentModel, String> colType;
    @FXML private TableColumn<DocumentModel, LocalDateTime> colUploadedAt;

    private final DocumentServiceClient client = new DocumentServiceClient();
    private final ObservableList<DocumentModel> data = FXCollections.observableArrayList();

    private Long currentDeviceId;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getId()));

        colFilename.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getFilename()));

        colType.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getFileType()));

        colUploadedAt.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getUploadedAt()));

        documentsTable.setItems(data);

        btnUpload.setOnAction(e -> uploadDocument());
        btnDownload.setOnAction(e -> downloadSelected());
    }

    /**
     * Καλείται από το MainController όταν ο χρήστης επιλέγει τη συσκευή
     */
    public void refresh(Long deviceId) {
        this.currentDeviceId = deviceId;
        data.clear();
        if (deviceId != null) {
            loadDocuments(deviceId);
        }
    }

    private void loadDocuments(Long deviceId) {
        List<DocumentModel> docs = client.getDocumentsByDevice(deviceId);
        data.setAll(docs);
    }

    /**
     * Ανεβάστε χρησιμοποιώντας multipart/form-data
     */
    private void uploadDocument() {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Ανεβάστε έγγραφο");
            File file = fc.showOpenDialog(btnUpload.getScene().getWindow());

            if (file == null) return;

            client.uploadDocument((long) currentDeviceId, file);

            loadDocuments(currentDeviceId);
            showInfo("Ανεβασμένο", "Το έγγραφο μεταφορτώθηκε με επιτυχία.");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Σφάλμα μεταφόρτωσης");
        }

        /*boolean ok = client.uploadDocument(currentDeviceId, file);

        if (ok) {
            loadDocuments(currentDeviceId);
            showInfo("Η μεταφόρτωση ολοκληρώθηκε", "Το έγγραφο μεταφορτώθηκε με επιτυχία.");
        } else {
            showError("Η μεταφόρτωση απέτυχε.");
        }*/
    }

    /**
     * Λήψη με χρήση δυαδικού αρχείου GET
     */
    private void downloadSelected() {
        DocumentModel selected = documentsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Επιλέξτε ένα έγγραφο για λήψη.");
            return;
        }

        byte[] fileBytes = client.downloadDocument(selected.getId());

        if (fileBytes == null) {
            showError("Αποτυχία λήψης εγγράφου.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setInitialFileName(selected.getFilename());
        File saveFile = fc.showSaveDialog(null);
        if (saveFile == null) return;

        try {
            Files.write(saveFile.toPath(), fileBytes);
            showInfo("Αποθηκεύτηκε", "Το έγγραφο αποθηκεύτηκε με επιτυχία.");
        } catch (Exception e) {
            showError("Αποτυχία αποθήκευσης αρχείου: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String msg, String details) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, details, ButtonType.OK);
        alert.setTitle(msg);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

/*
public class DocumentsController {

    @FXML private Button btnUpload;
    @FXML private Button btnDownload;

    @FXML private TableView<DocumentModel> documentsTable;

    @FXML private TableColumn<DocumentModel, Long> colId;
    @FXML private TableColumn<DocumentModel, String> colFilename;
    @FXML private TableColumn<DocumentModel, String> colType;
    @FXML private TableColumn<DocumentModel, LocalDateTime> colUploadedAt;

    private final DocumentServiceClient client = new DocumentServiceClient();
    private final ObservableList<DocumentModel> data = FXCollections.observableArrayList();


    */
/**
     * Runs only once when FXML is loaded.
     *//*

    @FXML
    public void initialize() {

        // Bind columns to model fields
        colId.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getId()));

        colFilename.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getFilename()));

        colType.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getFileType()));

        colUploadedAt.setCellValueFactory(val ->
                new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getUploadedAt()));

        documentsTable.setItems(data);

        btnUpload.setOnAction(e -> uploadDocument());
        btnDownload.setOnAction(e -> downloadSelected());
    }


    */
/**
     * Called by MainController every time a new device is selected.
     *//*

    public void refresh(Long deviceId) {
        data.clear();
        if (deviceId != null) {
            loadDocuments(deviceId);
        }
    }


    */
/**
     * Loads document list from backend.
     *//*

    private void loadDocuments(Long deviceId) {
        List<DocumentModel> docs = client.getDocumentsByDevice(deviceId);
        data.setAll(docs);
    }


    */
/**
     * Upload document through FileChooser.
     *//*

    private void uploadDocument() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Document");

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return;

        try {
            byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());

            DocumentModel doc = new DocumentModel();
            doc.setDeviceId(MainController.SelectionContext.selectedDeviceId);
            doc.setFilename(file.getName());
            doc.setFileType(getExtension(file));
            doc.setData(bytes);

            DocumentModel saved = client.uploadDocument(doc);

            if (saved != null) {
                data.add(saved); // update UI immediately
            }

        } catch (Exception ex) {
            showError("Upload failed: " + ex.getMessage());
        }
    }


    */
/**
     * Download selected document.
     *//*

    private void downloadSelected() {
        DocumentModel selected = documentsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Select a document to download.");
            return;
        }

        DocumentModel fullDoc = client.downloadDocument(selected.getId());

        if (fullDoc == null || fullDoc.getData() == null) {
            showError("Failed to download document.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Save File");
        fc.setInitialFileName(fullDoc.getFilename());

        File saveFile = fc.showSaveDialog(null);
        if (saveFile == null) return;

        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            fos.write(fullDoc.getData());
        } catch (Exception ex) {
            showError("Failed to save file: " + ex.getMessage());
        }
    }


    private String getExtension(File f) {
        String name = f.getName();
        int idx = name.lastIndexOf('.');
        return (idx > 0) ? name.substring(idx + 1) : "";
    }


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
*/
