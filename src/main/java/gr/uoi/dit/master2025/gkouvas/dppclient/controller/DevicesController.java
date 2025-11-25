package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class DevicesController {

    @FXML private TableView<DeviceModel> devicesTable;


    @FXML private TableColumn<DeviceModel, String> nameColumn;
    @FXML private TableColumn<DeviceModel, String> typeColumn;
    @FXML private TableColumn<DeviceModel, String> serialColumn;
    @FXML private TableColumn<DeviceModel, Long> buildingIdColumn;

    private final DeviceServiceClient deviceClient = new DeviceServiceClient();

    @FXML
    public void initialize() {
        setupContextMenu();


        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        serialColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        buildingIdColumn.setCellValueFactory(new PropertyValueFactory<>("buildingId"));

        devicesTable.setItems(FXCollections
                .observableArrayList(deviceClient.getAllDevices()));
    }

    private void setupContextMenu() {

        devicesTable.setRowFactory(tv -> {
            TableRow<DeviceModel> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Επεξεργασία συσκευής");


            edit.setOnAction(e -> onEditDevice(row.getItem()));

            menu.getItems().addAll(edit);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            return row;
        });



    }

    private void onEditDevice(DeviceModel row) {
        if (row == null) return;

        DeviceModel d = deviceClient.getDevice(row.getDeviceId());
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
}
