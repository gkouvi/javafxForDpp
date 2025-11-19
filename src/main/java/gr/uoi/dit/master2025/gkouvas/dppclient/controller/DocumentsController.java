package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DocumentModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DocumentServiceClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for the Documents tab.
 * Handles display, upload and download of documents.
 *
 * NOTE:
 * - initialize() runs once (UI setup)
 * - refresh(deviceId) runs EVERY time user selects another device
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


    /**
     * Runs only once when FXML is loaded.
     */
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


    /**
     * Called by MainController every time a new device is selected.
     */
    public void refresh(Long deviceId) {
        data.clear();
        if (deviceId != null) {
            loadDocuments(deviceId);
        }
    }


    /**
     * Loads document list from backend.
     */
    private void loadDocuments(Long deviceId) {
        List<DocumentModel> docs = client.getDocumentsByDevice(deviceId);
        data.setAll(docs);
    }


    /**
     * Upload document through FileChooser.
     */
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


    /**
     * Download selected document.
     */
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
