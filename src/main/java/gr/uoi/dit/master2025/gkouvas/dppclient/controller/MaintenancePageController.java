package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.MaintenanceServiceClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MaintenancePageController {

    @FXML private TableView<MaintenanceModel> maintenanceTable;

    @FXML private TableColumn<MaintenanceModel, Long> colTarget;
    @FXML private TableColumn<MaintenanceModel, String> technicianColumn;
    @FXML private TableColumn<MaintenanceModel, String> dateColumn;
    @FXML private TableColumn<MaintenanceModel, String> descriptionColumn;

    private final MaintenanceServiceClient maintenanceClient = new MaintenanceServiceClient();

    @FXML
    public void initialize() {

        colTarget.setCellValueFactory(new PropertyValueFactory<>("targetName"));
        technicianColumn.setCellValueFactory(new PropertyValueFactory<>("technician"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("maintenanceDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        maintenanceTable.setItems(FXCollections
                .observableArrayList(maintenanceClient.getAll()));
    }
}
