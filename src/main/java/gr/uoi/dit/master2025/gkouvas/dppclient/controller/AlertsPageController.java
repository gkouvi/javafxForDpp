package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.AlertServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertsPageController {


    @FXML private TableView<AlertModel> alertsTable;


    @FXML private TableColumn<AlertModel, String> deviceColumn;
    @FXML private TableColumn<AlertModel, String> messageColumn;
    @FXML private TableColumn<AlertModel, String> timestampColumn;
    @FXML private TableColumn<AlertModel, String> aTypeCol;

    private final AlertServiceClient alertClient = new AlertServiceClient();
    private final DeviceServiceClient deviceClient = new DeviceServiceClient();


    @FXML
    public void initialize() {


        setupAlertTable();
        alertsTable.getSortOrder().add(timestampColumn);
        alertsTable.setItems(FXCollections
                .observableArrayList(alertClient.getAllAlerts()));
        setupContextMenu();
    }


    private void setupContextMenu() {

        alertsTable.setRowFactory(tv -> {
            TableRow<AlertModel> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Επεξεργασία Ειδοποίησης");


            edit.setOnAction(e -> onEditAlert(row.getItem()));


            menu.getItems().addAll(edit );

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            return row;
        });



    }
    private void onEditAlert(AlertModel row) {
        if (row == null) return;

        AlertModel d = alertClient.getAllertFromDeviceID((row.getDeviceId()));
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit-alert.fxml"));
            Parent root = loader.load();

            EditAlertController controller = loader.getController();
            controller.loadAlert(d);

            Stage stage = new Stage();
            stage.setTitle("Επεξεργασία Ειδοποίησης");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setupAlertTable() {
        deviceColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeviceName()));
        messageColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMessage()));
        timestampColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDueDate().toString()));
        aTypeCol.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getStatus()));
        timestampColumn.setSortType(TableColumn.SortType.DESCENDING);

        // === Custom renderer for severity / type ===
        aTypeCol.setCellFactory(column -> new TableCell<AlertModel, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label badge = new Label(type);
                badge.setPrefWidth(100);
                badge.getStyleClass().add("alert-badge");

                switch (type.toUpperCase()) {
                    case "OFFLINE":
                    case "CRITICAL":
                        badge.getStyleClass().add("badge-offline"); break;
                    case "ONLINE":
                    case "INFO":
                        badge.getStyleClass().add("badge-online"); break;
                    case "PING_TIMEOUT":
                    case "WARN":
                        badge.getStyleClass().add("badge-warning");break;
                    default:
                        badge.getStyleClass().add("badge-info"); break;
                }

                setGraphic(badge);
            }
        });

        // Double-click opens device card
        alertsTable.setRowFactory(tv -> {
            TableRow<AlertModel> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    AlertModel alert = row.getItem();
                    openDeviceCard(alert.getDeviceId());
                }
            });
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

}
