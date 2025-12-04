package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.OverallHealthModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DevicesController {

    @FXML private TableColumn<DeviceModel, Boolean> onlineCol;
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
        buildingIdColumn.setCellValueFactory(new PropertyValueFactory<>("buildingName"));
        onlineCol.setCellValueFactory(c ->new SimpleBooleanProperty(c.getValue().isOnline()));

        onlineCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean online, boolean empty) {
                super.updateItem(online, empty);

                if (empty || online == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(online ? "🟢" : "🔴");
               // badge.setStyle("-fx-font-size: 20;");

                //Label badge = new Label();
                badge.setStyle("-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 10;");

                if (online) {
                    badge.setText("ONLINE");
                    badge.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 12;");
                } else {
                    badge.setText("OFFLINE");
                    badge.setStyle("-fx-font-size: 20;-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 12;");
                }

                setGraphic(badge);
                setText(null);
            }
        });



        devicesTable.setItems(FXCollections
                .observableArrayList(deviceClient.getAllDevices()));
    }

    private void setupContextMenu() {

        devicesTable.setRowFactory(tv -> {
            TableRow<DeviceModel> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Επεξεργασία συσκευής");
            MenuItem openCard = new MenuItem("Άνοιγμα Κάρτας Συσκευής");


            edit.setOnAction(e -> onEditDevice(row.getItem()));
            openCard.setOnAction(e->openDeviceCard(row.getItem().getDeviceId()));

            menu.getItems().addAll(edit,openCard);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            return row;
        });



    }
    public void openDeviceCard(Long deviceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deviceCard.fxml"));
            Parent root = loader.load();


            DeviceCardController controller = loader.getController();
            controller.loadDevice(deviceId);

            Stage stage = new Stage();
            stage.setTitle("Συσκευή " + deviceClient.getDevice(deviceId).getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/css/app.css").toExternalForm()
            );
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
