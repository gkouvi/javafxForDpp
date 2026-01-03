package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceInterval;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.UpcomingMaintenanceItem;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.MaintenanceServiceClient;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class MaintenancePageController {

    public TableView upcomingMaintenanceTable;
    @FXML private TableColumn<UpcomingMaintenanceItem, String> upNameCol;
    @FXML private TableColumn<UpcomingMaintenanceItem, MaintenanceInterval> upIntervalCol;
    @FXML private TableColumn<UpcomingMaintenanceItem, LocalDate> upNextDateCol;
    @FXML private TableView<MaintenanceModel> maintenanceTable;

    @FXML private TableColumn<MaintenanceModel, Long> colTarget;
    @FXML private TableColumn<MaintenanceModel, String> technicianColumn;
    @FXML private TableColumn<MaintenanceModel, String> dateColumn;
    @FXML private TableColumn<MaintenanceModel, String> descriptionColumn;

    private final MaintenanceServiceClient maintenanceClient = new MaintenanceServiceClient();
    private final DeviceServiceClient deviceClient = new DeviceServiceClient();

    @FXML
    public void initialize() {

        colTarget.setCellValueFactory(new PropertyValueFactory<>("targetName"));
        technicianColumn.setCellValueFactory(new PropertyValueFactory<>("technician"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("maintenanceDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        maintenanceTable.setItems(FXCollections
                .observableArrayList(maintenanceClient.getAll()));
        setupUpcomingTable();

        upcomingMaintenanceTable.setRowFactory(table -> {

            TableRow<UpcomingMaintenanceItem> row = new TableRow<>() {
                @Override
                protected void updateItem(UpcomingMaintenanceItem item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setStyle("");
                        return;
                    }

                    LocalDate next = item.getNextMaintenanceDate();
                    LocalDate today = LocalDate.now();

                    long days = ChronoUnit.DAYS.between(today, next);

                    if (days <= 3) {
                        setStyle("-fx-background-color: #8B0000; -fx-text-fill: white;");
                    } else if (days <= 7) {
                        setStyle("-fx-background-color: #FF8C00; -fx-text-fill: black;");
                    } else if (days <= 14) {
                        setStyle("-fx-background-color: #FFD700; -fx-text-fill: black;");
                    } else {
                        setStyle("-fx-background-color: #006400; -fx-text-fill: white;");
                    }
                }
            };

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {

                    UpcomingMaintenanceItem item = row.getItem();

                    // ΑΡΙΣΤΕΡΟ ΚΛΙΚ
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                        openDeviceCard(item.getDeviceId());
                    }

                    // ΔΕΞΙ ΚΛΙΚ
                    if (event.getButton() == MouseButton.SECONDARY) {
                        showMaintenanceContextMenu(row, item, event.getScreenX(), event.getScreenY());
                    }
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
    private void setupUpcomingTable() {



        upNameCol.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        upIntervalCol.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getInterval()));

        upNextDateCol.setCellValueFactory(new PropertyValueFactory<>("nextMaintenanceDate"));

        refreshMaintenanceTable();
    }
    private void showMaintenanceContextMenu(TableRow<UpcomingMaintenanceItem> row,
                                            UpcomingMaintenanceItem item,
                                            double x, double y) {

        ContextMenu menu = new ContextMenu();

        MenuItem createNow = new MenuItem("Δημιουργία Συντήρησης Τώρα");
        createNow.setOnAction(e -> openCreateMaintenanceDialog(item));

        MenuItem openDevice = new MenuItem("Άνοιγμα Συσκευής");
        openDevice.setOnAction(e -> openDeviceCard(item.getDeviceId()));

        menu.getItems().addAll(createNow, openDevice);

        menu.show(row, x, y);
    }
    private void openCreateMaintenanceDialog(UpcomingMaintenanceItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MaintenanceCreateDialog.fxml"));
            Parent root = loader.load();

            MaintenanceCreateDialogController controller = loader.getController();
            controller.setDeviceId(item.getDeviceId());
            //controller.prefillInterval(item.getInterval());    // optional
            //controller.prefillDate(LocalDate.now());

            Stage stage = new Stage();
            stage.setTitle("Νέα Συντήρηση");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshMaintenanceTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshMaintenanceTable() {
        List<UpcomingMaintenanceItem> list = deviceClient.getUpcomingMaintenanceDetails();
        upcomingMaintenanceTable.getItems().setAll(list);
    }



}
