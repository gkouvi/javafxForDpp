package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BuildingsController {

    @FXML private TableView<BuildingModel> buildingsTable;
    @FXML private TableColumn<BuildingModel, Long> idColumn;
    @FXML private TableColumn<BuildingModel, String> nameColumn;
    @FXML private TableColumn<BuildingModel, String> addressColumn;
    @FXML private TableColumn<BuildingModel, Long> siteIdColumn;

    private final BuildingServiceClient buildingClient = new BuildingServiceClient();

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        siteIdColumn.setCellValueFactory(new PropertyValueFactory<>("siteId"));

        buildingsTable.setItems(FXCollections
                .observableArrayList(buildingClient.getAllBuildings()));
    }
}
