package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DevicesController {

    @FXML private TableView<DeviceModel> devicesTable;

    @FXML private TableColumn<DeviceModel, Long> idColumn;
    @FXML private TableColumn<DeviceModel, String> nameColumn;
    @FXML private TableColumn<DeviceModel, String> typeColumn;
    @FXML private TableColumn<DeviceModel, String> serialColumn;
    @FXML private TableColumn<DeviceModel, Long> buildingIdColumn;

    private final DeviceServiceClient deviceClient = new DeviceServiceClient();

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("deviceId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        serialColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        buildingIdColumn.setCellValueFactory(new PropertyValueFactory<>("buildingId"));

        devicesTable.setItems(FXCollections
                .observableArrayList(deviceClient.getAllDevices()));
    }
}
