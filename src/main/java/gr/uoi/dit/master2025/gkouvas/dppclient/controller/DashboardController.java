package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.service.PingMonitorService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
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

    @FXML private TableView<DeviceModel> upcomingMaintenanceTable;

    @FXML private TableColumn<DeviceModel, String> upNameCol;
    @FXML private TableColumn<DeviceModel, String> upIntervalCol;
    @FXML private TableColumn<DeviceModel, LocalDate> upNextDateCol;


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
        loadUpcomingMaintenance();

        List<DeviceModel> allDevices = deviceClient.getAllDevices();
        Thread pingThread = new Thread(new PingMonitorService(allDevices));
        pingThread.start();
       /* alertsChart.getStylesheets().add(
                getClass().getResource("/css/charts.css").toExternalForm()
        );

        maintenanceChart.getStylesheets().add(
                getClass().getResource("/css/charts.css").toExternalForm()
        );*/

    }

    // ===================================================================
    // TABLE SETUP
    // ===================================================================
    private void setupTables() {
        // Alerts table
        //System.out.println(latestAlertsTable.getColumns().isEmpty());
        colAlertDevice.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colAlertMsg.setCellValueFactory(new PropertyValueFactory<>("message"));

        colMaintBuilding.setCellValueFactory(new PropertyValueFactory<>("buildingName"));
        colMaintTech.setCellValueFactory(new PropertyValueFactory<>("technician"));
        colMaintDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
       /* if (!latestAlertsTable.getColumns().isEmpty()) {
            // = new TableColumn<>("Device");
            deviceCol.setCellValueFactory(new PropertyValueFactory<>("deviceName"));


            //TableColumn<AlertModel, String> msgCol = new TableColumn<>("Message");
            msgCol.setCellValueFactory(new PropertyValueFactory<>("message"));

            //latestAlertsTable.getColumns().addAll(deviceCol, msgCol);



        }

        // Maintenance table
        if (!latestMaintTable.getColumns().isEmpty()) {
            // = new TableColumn<>("Target");
            targetCol.setCellValueFactory(new PropertyValueFactory<>("targetName"));

            //TableColumn<MaintenanceModel, String> descCol = new TableColumn<>("Description");
            descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

            //latestMaintTable.getColumns().addAll(targetCol, descCol);

        }
*/

    }

    private void setupUpcomingTable() {
        upNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        upIntervalCol.setCellValueFactory(new PropertyValueFactory<>("maintenanceInterval"));
        upNextDateCol.setCellValueFactory(new PropertyValueFactory<>("nextMaintenanceDate"));
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

            maintenanceCount.setText(String.valueOf(maintenanceClient.getAll().size()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================================================================
    // ALERTS BAR CHART
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
    // MAINTENANCE LINE CHART
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
    // Latest Alerts (last 10)
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
    // Latest Maintenance (last 10)
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
    private void loadUpcomingMaintenance() {
        List<DeviceModel> devices = deviceClient.getUpcomingMaintenance();

        upcomingMaintenanceTable.getItems().setAll(devices);
    }

}
