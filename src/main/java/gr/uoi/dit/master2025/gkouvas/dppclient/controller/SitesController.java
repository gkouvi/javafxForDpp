package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.SiteModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;



public class SitesController {

    @FXML private TableView<SiteModel> sitesTable;
    @FXML private TableColumn<SiteModel, Long> idColumn;
    @FXML private TableColumn<SiteModel, String> nameColumn;
    @FXML private TableColumn<SiteModel, String> regionColumn;
    @FXML private TableColumn<SiteModel, String> coordinatesColumn;

    private final SiteServiceClient siteClient = new SiteServiceClient();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        coordinatesColumn.setCellValueFactory(new PropertyValueFactory<>("coordinates"));

        sitesTable.setItems(FXCollections.observableArrayList(siteClient.getAllSites()));
    }
}


