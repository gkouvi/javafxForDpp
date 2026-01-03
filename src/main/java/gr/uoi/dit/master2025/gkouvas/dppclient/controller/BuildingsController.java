package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class BuildingsController {

    @FXML private TableView<BuildingModel> buildingsTable;
    @FXML private TableColumn<BuildingModel, Long> idColumn;
    @FXML private TableColumn<BuildingModel, String> nameColumn;
    @FXML private TableColumn<BuildingModel, String> addressColumn;
    @FXML private TableColumn<BuildingModel, Long> siteIdColumn;

    private final BuildingServiceClient buildingClient = new BuildingServiceClient();

    @FXML
    public void initialize() {
        setupContextMenu();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        siteIdColumn.setCellValueFactory(new PropertyValueFactory<>("siteFromID"));

        buildingsTable.setItems(FXCollections
                .observableArrayList(buildingClient.getAllBuildings()));
    }

    private void setupContextMenu() {

        buildingsTable.setRowFactory(tv -> {
            TableRow<BuildingModel> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Επεξεργασία Κτιρίου");
            MenuItem buildingCard = new MenuItem("Άνοιγμα Κάρτας Κτιρίου");


            edit.setOnAction(e -> onEditBuilding(row.getItem()));
            buildingCard.setOnAction(event->onCardBuildingOpen(row.getItem()));

            menu.getItems().addAll(edit);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            return row;
        });



    }

    private void onCardBuildingOpen(BuildingModel item) {
        if (item == null) return;

        BuildingModel d = buildingClient.getBuilding((item.getId()));
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/buildingCard.fxml"));
            Parent root = loader.load();

            BuildingCardController controller = loader.getController();
            controller.loadBuilding((d.getId()));

            Stage stage = new Stage();
            stage.setTitle("ΠΠληροφορίες  Κτιρίου");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onEditBuilding(BuildingModel row) {
        if (row == null) return;

        BuildingModel d = buildingClient.getBuilding((row.getId()));
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_building.fxml"));
            Parent root = loader.load();

            EditBuildingController controller = loader.getController();
            controller.loadBuilding((d));

            Stage stage = new Stage();
            stage.setTitle("Επεξεργασία Κτιρίου");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
