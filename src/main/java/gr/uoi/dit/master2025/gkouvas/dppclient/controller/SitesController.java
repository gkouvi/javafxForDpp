package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.SiteModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class SitesController {

    @FXML private TableView<SiteModel> sitesTable;
    @FXML private TableColumn<SiteModel, Long> idColumn;
    @FXML private TableColumn<SiteModel, String> nameColumn;
    @FXML private TableColumn<SiteModel, String> regionColumn;
    @FXML private TableColumn<SiteModel, String> coordinatesColumn;

    private final SiteServiceClient siteClient = new SiteServiceClient();

    @FXML
    public void initialize() {
        setupContextMenu();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        coordinatesColumn.setCellValueFactory(new PropertyValueFactory<>("coordinates"));

        sitesTable.setItems(FXCollections.observableArrayList(siteClient.getAllSites()));
    }

    private void setupContextMenu() {

        sitesTable.setRowFactory(tv -> {
            TableRow<SiteModel> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Επεξεργασία Μονάδας");


            edit.setOnAction(e -> onEditBuilding(row.getItem()));

            menu.getItems().addAll(edit);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            return row;
        });



    }

    private void onEditBuilding(SiteModel row) {
        if (row == null) return;

        SiteModel d = siteClient.getSite((row.getId()));
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_site.fxml"));
            Parent root = loader.load();

            EditSiteController controller = loader.getController();
            controller.loadSite((d));

            Stage stage = new Stage();
            stage.setTitle("Επεξεργασία Μονάδας");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


