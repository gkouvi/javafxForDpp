package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.service.PingMonitorService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.naturalOrder;

public class DashboardController {


    // ===== KPIs =====
    @FXML private Label sitesCount;
    @FXML private Label buildingsCount;
    @FXML private Label devicesCount;
    @FXML private Label alertsCount;
    @FXML private Label maintenanceCount;

    // ===== CHARTS =====
    @FXML private BarChart<String, Number> alertsChart;
    @FXML private LineChart<String, Number> maintenanceChart;

    // ===== TABLES =====

    @FXML private TableView<AlertModel> alertsTable;
    @FXML private TableColumn<AlertModel, String> colAlertDevice;
    @FXML private TableColumn<AlertModel, String> colAlertMsg;

    @FXML private TableView<MaintenanceModel> maintenanceTable;
    @FXML private TableColumn<MaintenanceModel, String> colMaintBuilding;
    @FXML private TableColumn<MaintenanceModel, String> colMaintTech;
    @FXML private TableColumn<MaintenanceModel, String> colMaintDesc;

    @FXML private TableView<UpcomingMaintenanceItem> upcomingMaintenanceTable;

    @FXML private TableColumn<UpcomingMaintenanceItem, String> upNameCol;
    @FXML private TableColumn<UpcomingMaintenanceItem, MaintenanceInterval> upIntervalCol;
    @FXML private TableColumn<UpcomingMaintenanceItem, LocalDate> upNextDateCol;

    @FXML private Label urgentKpiLabel;
    @FXML private Label criticalKpiLabel;
    @FXML private Label overdueKpiLabel;
    @FXML private Label monthKpiLabel;

    // REST Clients
    private final SiteServiceClient siteClient = new SiteServiceClient();
    private final BuildingServiceClient buildingClient = new BuildingServiceClient();
    private final DeviceServiceClient deviceClient = new DeviceServiceClient();
    private final AlertServiceClient alertClient = new AlertServiceClient();
    private final MaintenanceServiceClient maintenanceClient = new MaintenanceServiceClient();

    @FXML
    public void initialize() {
        setupTables();
        loadKPIs();
        loadAlertsChart();
        loadMaintenanceChart();
        loadLatestAlertsTable();
        loadLatestMaintenanceTable();
        setupUpcomingTable();
        upIntervalCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(MaintenanceInterval  intervalName, boolean empty) {
                super.updateItem(intervalName, empty);

                if (empty || intervalName == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                UpcomingMaintenanceItem item = getTableView().getItems().get(getIndex());
                LocalDate next = item.getNextMaintenanceDate();
                LocalDate today = LocalDate.now();

                long days = ChronoUnit.DAYS.between(today, next);

                // ICON (colored circle)
                Label icon = new Label("●");
                icon.setStyle("-fx-font-size: 16px; -fx-padding: 0 5 0 0;");

                if (next.isBefore(today)) {
                    icon.setStyle("-fx-text-fill: #B30000; -fx-font-size: 16; -fx-padding: 0 5 0 0;");
                }
                else if (days <= 3) {
                    icon.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 16; -fx-padding: 0 5 0 0;");
                }
                else if (days <= 7) {
                    icon.setStyle("-fx-text-fill: #FF8C00; -fx-font-size: 16; -fx-padding: 0 5 0 0;");
                }
                else if (days <= 14) {
                    icon.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 16; -fx-padding: 0 5 0 0;");
                }
                else {
                    icon.setStyle("-fx-text-fill: #00FF7F; -fx-font-size: 16; -fx-padding: 0 5 0 0;");
                }

                Label text = new Label(intervalName.name());
                text.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

                HBox box = new HBox(icon, text);
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
                setText(null);
                Tooltip.install(box, new Tooltip(
                        "Επόμενη συντήρηση: " + next +
                                "\nΣε " + days + " ημέρες"
                ));

            }
        });


        upcomingMaintenanceTable.setRowFactory(table -> new TableRow<>() {
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
        });

        upcomingMaintenanceTable.setRowFactory(table -> {
            TableRow<UpcomingMaintenanceItem> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (! row.isEmpty()) {

                    UpcomingMaintenanceItem item = row.getItem();

                    // ΑΡΙΣΤΕΡΟ ΚΛΙΚ
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {

                        Long deviceId = item.getDeviceId();
                        openDeviceCard(deviceId);
                    }

                    // ΔΕΞΙ ΚΛΙΚ
                    if (event.getButton() == MouseButton.SECONDARY) {
                        showMaintenanceContextMenu(row, item, event.getScreenX(), event.getScreenY());
                    }
                }
            });

            return row;
        });


        List<DeviceModel> allDevices = deviceClient.getAllDevices();
        Thread pingThread = new Thread(new PingMonitorService(allDevices));
        pingThread.start();

    }

    // ===================================================================
    // TABLE ΡΥΘΜΙΣΗ
    // ===================================================================
    private void setupTables() {

        colAlertDevice.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colAlertMsg.setCellValueFactory(new PropertyValueFactory<>("message"));

        colMaintBuilding.setCellValueFactory(new PropertyValueFactory<>("buildingName"));
        colMaintTech.setCellValueFactory(new PropertyValueFactory<>("technician"));
        colMaintDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
           }

    private void setupUpcomingTable() {
        upNameCol.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        upIntervalCol.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getInterval()));

        upNextDateCol.setCellValueFactory(new PropertyValueFactory<>("nextMaintenanceDate"));

        refreshMaintenanceTable();
    }
    public void refreshMaintenanceTable() {
        List<UpcomingMaintenanceItem> list = deviceClient.getUpcomingMaintenanceDetails();
        upcomingMaintenanceTable.getItems().setAll(list);
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
            controller.prefillInterval(item.getInterval());    // optional
            controller.prefillDate(LocalDate.now());

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



    // ===================================================================
    // KPIs
    // ===================================================================
    private void loadKPIs() {
        try {
            List<SiteModel> sites = siteClient.getAllSites();
            sitesCount.setText(String.valueOf(sites.size()));

            long buildingTotal = sites.stream()
                    .mapToLong(s -> buildingClient.getBuildingsBySite(s.getId()).size())
                    .sum();
            buildingsCount.setText(String.valueOf(buildingTotal));

            long deviceTotal = sites.stream()
                    .flatMap(s -> buildingClient.getBuildingsBySite(s.getId()).stream())
                    .mapToLong(b -> deviceClient.getDevicesByBuilding(b.getId()).size())
                    .sum();
            devicesCount.setText(String.valueOf(deviceTotal));

            long alertTotal = sites.stream()
                    .flatMap(s -> buildingClient.getBuildingsBySite(s.getId()).stream())
                    .flatMap(b -> deviceClient.getDevicesByBuilding(b.getId()).stream())
                    .mapToLong(d -> alertClient.getAlertsForDevice(d.getDeviceId()).size())
                    .sum();
            alertsCount.setText(String.valueOf(alertTotal));
            MaintenanceKpiModel kpi = deviceClient.getMaintenanceKpis();

            urgentKpiLabel.setText(String.valueOf(kpi.getUrgent()));
            criticalKpiLabel.setText(String.valueOf(kpi.getCritical()));
            overdueKpiLabel.setText(String.valueOf(kpi.getOverdue()));
            monthKpiLabel.setText(String.valueOf(kpi.getThisMonth()));

            maintenanceCount.setText(String.valueOf(maintenanceClient.getAll().size()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================================================================
    // Ειδοποιήσεις BAR CHART
    // ===================================================================
    private void loadAlertsChart() {

        List<AlertModel> alerts = new ArrayList<>();



        // μαζεύουμε ΟΛΕΣ τις συσκευές → και όλα τα alerts
        siteClient.getAllSites().forEach(site ->
                buildingClient.getBuildingsBySite(site.getId())
                        .forEach(building ->
                                deviceClient.getDevicesByBuilding(building.getId())
                                        .forEach(device ->
                                                alerts.addAll(alertClient.getAlertsForDevice(device.getDeviceId()))
                                        )
                        )
        );

        Map<String, Long> counts = alerts.stream()
                .collect(Collectors.groupingBy(AlertModel::getDeviceName, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ειδοποιήσεις ανά συσκευή");

        counts.forEach((device, total) ->
                series.getData().add(new XYChart.Data<>(device, total)));

        alertsChart.getData().clear();
        alertsChart.getData().add(series);
        alertsChart.applyCss();
        alertsChart.layout();

        NumberAxis yAxis = (NumberAxis) alertsChart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setTickUnit(1);

        double max = series.getData().stream()
                .mapToDouble(d -> d.getYValue().doubleValue())
                .max().orElse(5);

        yAxis.setUpperBound(Math.max(5, max + 1));

    }

    // ===================================================================
    // ΣΥΝΤΗΡΗΣΗ LINE CHART
    // ===================================================================
    private void loadMaintenanceChart() {

        List<MaintenanceModel> logs = maintenanceClient.getAll();



        Map<String, Long> perMonth =
                logs.stream()
                        .filter(log -> log.getMaintenanceDate() != null)
                        .collect(Collectors.groupingBy(
                                l -> l.getMaintenanceDate().getMonth().toString(),
                                Collectors.counting()
                        ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Δραστηριότητα Συντήρησης");

        perMonth.forEach((month, total) ->
                series.getData().add(new XYChart.Data<>(month, total)));

        maintenanceChart.getData().clear();
        maintenanceChart.getData().add(series);

        maintenanceChart.applyCss();
        maintenanceChart.layout();

        NumberAxis yAxis = (NumberAxis) maintenanceChart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setTickUnit(1);

        double max = series.getData().stream()
                .mapToDouble(d -> d.getYValue().doubleValue())
                .max().orElse(5);

        yAxis.setUpperBound(Math.max(5, max + 1));

    }

    // ===================================================================
    // Τελευταίες ειδοποιήσεις (Τελευταίες 10)
    // ===================================================================
    private void loadLatestAlertsTable() {

        List<AlertModel> all = new ArrayList<>();


        siteClient.getAllSites().forEach(site ->
                buildingClient.getBuildingsBySite(site.getId()).forEach(building ->
                        deviceClient.getDevicesByBuilding(building.getId()).forEach(device ->
                                all.addAll(alertClient.getAlertsForDevice(device.getDeviceId()))
                        )));

        alertsTable.getItems().setAll(
                all.stream()
                        .sorted(Comparator.comparing(AlertModel::getDueDate,Comparator.nullsLast(naturalOrder())).reversed())
                       .limit(10)
                       .toList()
        );
    }

    // ===================================================================
    // Τελευταία συντήρηση (τελευταία 10)
    // ===================================================================
    private void loadLatestMaintenanceTable() {

        List<MaintenanceModel> logs = maintenanceClient.getAll();
        for (MaintenanceModel m : logs) {

            if (m.getDeviceId() != null) {
                m.setDeviceName(
                        deviceClient.getDevice(m.getDeviceId()).getName()
                );
            }

            if (m.getBuildingId() != null) {
                m.setBuildingName(
                        buildingClient.getBuilding(m.getBuildingId()).getName()
                );
            }
        }




        maintenanceTable.getItems().setAll(
                logs.stream()
                        .sorted(Comparator.comparing(MaintenanceModel::getMaintenanceDate).reversed())
                        .limit(10)
                        .toList()
        );
    }


}
