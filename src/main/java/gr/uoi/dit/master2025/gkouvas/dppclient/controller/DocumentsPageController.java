package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.DocumentModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DocumentServiceClient;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class DocumentsPageController {

    @FXML
    private TableView<DeviceDocumentRow> documentsTable;

    @FXML
    private TableColumn<DeviceDocumentRow, String> deviceColumn;
    @FXML
    private TableColumn<DeviceDocumentRow, String> statusColumn;

    private final DeviceServiceClient deviceClient = new DeviceServiceClient();
    private final DocumentServiceClient documentClient = new DocumentServiceClient();

    @FXML
    public void initialize() {
        deviceColumn.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("documentStatus"));
        deviceColumn.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("documentStatus"));

        setupContextMenu();
        loadData();
    }

    public static class DeviceDocumentRow {
        private final String deviceName;
        private final String documentStatus;
        private final Long deviceID;

        public DeviceDocumentRow(String deviceName, String documentStatus, Long deviceID) {
            this.deviceName = deviceName;
            this.documentStatus = documentStatus;
            this.deviceID = deviceID;
        }

        public Long getDeviceID() {
            return deviceID;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public String getDocumentStatus() {
            return documentStatus;
        }
    }

    private void loadData() {

        List<DeviceModel> devices = deviceClient.getAllDevices();

        var rows = FXCollections.<DeviceDocumentRow>observableArrayList();

        for (DeviceModel d : devices) {

            List<DocumentModel> docs = documentClient.getDocumentsByDevice(d.getDeviceId());

            String status = docs.isEmpty() ? "✖ Όχι" : "✔ Ναι (" + docs.size() + ")";

            rows.add(new DeviceDocumentRow(
                    d.getName(),
                    status,d.getDeviceId()
            ));
        }

        documentsTable.setItems(rows);
    }

    // ---------------------------
    // INNER MODEL FOR TABLE ROW
    // ---------------------------


    private void setupContextMenu() {

        documentsTable.setRowFactory(tv -> {
            TableRow<DeviceDocumentRow> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Edit Device");
            MenuItem upload = new MenuItem("Upload Document");
            MenuItem download = new MenuItem("Download Documents");

            edit.setOnAction(e -> onEditDevice(row.getItem()));
            upload.setOnAction(e -> onUpload(row.getItem()));
            download.setOnAction(e -> onDownload(row.getItem()));

            menu.getItems().addAll(edit, upload, download);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            return row;
        });



    }

   /* private void onEditDevice(DeviceDocumentRow item) {


        Long id = item.getDeviceID();
        if (id == null) {
            showWarning("Please select a device.");
            return;
        }

        try {
            DeviceModel d = deviceClient.getDevice(id);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_device.fxml"));
            Parent root = loader.load();

            EditDeviceController controller = loader.getController();
            controller.loadDevice(d);

            Stage stage = new Stage();
            stage.setTitle("Edit Device");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    private void onEditDevice(DeviceDocumentRow row) {
        if (row == null) return;

        DeviceModel d = deviceClient.getDevice(row.getDeviceID());
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_device.fxml"));
            Parent root = loader.load();

            EditDeviceController controller = loader.getController();
            controller.loadDevice(d);

            Stage stage = new Stage();
            stage.setTitle("Επεξεργασία συσκευής");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onUpload(DeviceDocumentRow row) {
        if (row == null) return;

        //DeviceModel d = deviceClient.getDeviceByName(row.getDeviceName());
        //OpenWindows.showUploadDocumentWindow(d.getDeviceId());
    }

    private void onDownload(DeviceDocumentRow row) {
        if (row == null) return;

        //DeviceModel d = deviceClient.getDeviceByName(row.getDeviceName());
        //OpenWindows.showDocumentListWindow(d.getDeviceId());
    }

}



