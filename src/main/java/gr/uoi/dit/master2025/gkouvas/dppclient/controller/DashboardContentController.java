package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.ui.HeatmapComponent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardContentController {


    public CategoryAxis alertsXAxis;
    public NumberAxis alertsYAxis;

    public Pane heatmapOverlayPane;

    public VBox overdueBox;
    public LineChart dualMaintenanceChart;
    public NumberAxis leftYAxis;
    //public StackPane heatmapContainer;
    @FXML private Canvas heatmapCanvas;
       // ===== CHARTS =====
    @FXML private BarChart<String, Number> alertsChart;

    @FXML public StackPane riskGaugePane;

        // REST Clients
    private final SiteServiceClient siteClient = new SiteServiceClient();
    private final BuildingServiceClient buildingClient = new BuildingServiceClient();
    private final DeviceServiceClient deviceClient = new DeviceServiceClient();
    private final AlertServiceClient alertClient = new AlertServiceClient();
    private final MaintenanceServiceClient maintenanceClient = new MaintenanceServiceClient();
    private static final int CELL_W = 30;
    private static final int CELL_H = 25;
    public VBox dueMaintBox;
    public Label onlineLabel;
    public Label dueMaintLabel;
    public VBox riskBox;
    public Label riskLabel;
    @FXML private Label overdueLabel;
    public VBox onlineDevicesBox;
    public VBox offlineDevicesBox;
    public VBox uptimeBox;
    public Label offlineDevicesLabel;
    public Label uptimeLabel;
    OverallHealthModel health = deviceClient.getFleetHealth();
    private static final String[] WEEKDAYS = {
            "Κυρ", "Δευ", "Τρι", "Τετ", "Πεμ", "Παρ", "Σαβ"
    };


    // -----------------------------------------
    // INITIALIZE – καλείται μόλις φορτωθεί το FXML
    // -----------------------------------------
    @FXML
    public void initialize() {

        Long overdueMaint = loadMaintenanceKpi();
        loadHealthKpis();
        loadAlertsChart();
        loadHeatmap();
        styleChart(alertsChart);
        //styleChart(maintenanceChart);
        double riskPercent = computeRisk(health, overdueMaint);
        updateKpis();
        setupKpiTooltips();

        // Αποφυγή NullPointer
        if (dualMaintenanceChart != null) {
            dualMaintenanceChart.getStylesheets().add(getClass().getResource("/css/charts.css").toExternalForm());
            //loadMaintenanceForecast();
            loadDualMaintenanceChart();
        }

        if (riskGaugePane != null) {
            renderRiskGauge(riskPercent);// demo value
        }

    }


    private Long loadMaintenanceKpi() {
        List<MaintenanceModel> all = maintenanceClient.getAll();

        long thisMonth = all.stream()
                .filter(m -> m.getMaintenanceDate() != null &&
                        m.getMaintenanceDate().getMonth().equals(LocalDate.now().getMonth()))
                .count();

        long overdue = all.stream()
                .filter(m -> m.getPlannedDate() != null &&
                        m.getPlannedDate().isBefore(LocalDate.now()))
                .count();

        dueMaintLabel.setText(String.valueOf(thisMonth));
        overdueLabel.setText(String.valueOf(overdue));
        return overdue;
    }



    private void loadHealthKpis() {
        try {
            OverallHealthModel health = deviceClient.getFleetHealth();

            //totalDevicesLabel.setText(String.valueOf(health.totalDevices));
            onlineLabel.setText(String.valueOf(health.onlineCount));
            offlineDevicesLabel.setText(String.valueOf(health.offlineCount));
            uptimeLabel.setText(String.format("%.1f%%", health.fleetUptimePercent));

            // Dynamic coloring
            uptimeBox.getStyleClass().removeAll(
                    "kpi-uptime-green",
                    "kpi-uptime-yellow",
                    "kpi-uptime-orange",
                    "kpi-uptime-red"
            );

            if (health.fleetUptimePercent >= 97)
                uptimeBox.getStyleClass().add("kpi-uptime-green");
            else if (health.fleetUptimePercent >= 90)
                uptimeBox.getStyleClass().add("kpi-uptime-yellow");
            else
                uptimeBox.getStyleClass().add("kpi-uptime-orange");

            if (health.offlineCount > 0)
                offlineDevicesBox.getStyleClass().add("kpi-uptime-red");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------
    // RISK GAUGE WIDGET
    // -----------------------------------------
    private void renderRiskGauge(double riskPercent) {

        double size = 250;
        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double stroke = 22;
        double offset = stroke;       // padding from border
        double diameter = size - 2 * offset;
        double startAngle = 90;       // ξεκινάει από πάνω (όπως τα smartwatches)

        // --- BACKGROUND CIRCLE ---
        gc.setLineWidth(stroke);
        gc.setStroke(Color.web("#333333"));
        gc.strokeArc(
                offset, offset,
                diameter, diameter,
                startAngle,
                -360,                   // ολόκληρος κύκλος
                ArcType.OPEN
        );

        // --- VALUE ARC ---
        double angle = -360 * (riskPercent / 100.0); // clockwise

        Color gaugeColor =
                (riskPercent < 30) ? Color.LIMEGREEN :
                        (riskPercent < 60) ? Color.GOLD :
                                Color.RED;

        gc.setStroke(gaugeColor);
        gc.strokeArc(
                offset, offset,
                diameter, diameter,
                startAngle,
                angle,
                ArcType.OPEN
        );

        // --- TEXT (center) ---
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(24));
        String text = (int) riskPercent + "%";
        gc.fillText(text, size / 2 - 20, size / 2 + 8);

        riskGaugePane.getChildren().setAll(canvas);
    }




    // -----------------------------------------
    // MAINTENANCE FORECAST
    // -----------------------------------------
    private void loadDualMaintenanceChart() {
        MaintenanceBarChartStats stats = maintenanceClient.getMonthlyStats();
        if (stats == null) return;

        // --- gather month list (sorted)
        Set<String> allMonths = new TreeSet<>();

        if (stats.planned != null)
            stats.planned.forEach(m -> allMonths.add(m.getMonth()));
        if (stats.completed != null)
            stats.completed.forEach(m -> allMonths.add(m.getMonth()));

        // --- SERIES 1: PLANNED
        XYChart.Series<String, Number> plannedSeries = new XYChart.Series<>();
        plannedSeries.setName("Προγραμματισμένες");

        for (String m : allMonths) {
            long value = stats.planned.stream()
                    .filter(x -> x.getMonth().equals(m))
                    .map(MonthCountModel::getCount)
                    .findFirst()
                    .orElse(0L);

            plannedSeries.getData().add(new XYChart.Data<>(m, value));
        }

        // --- SERIES 2: COMPLETED
        XYChart.Series<String, Number> completedSeries = new XYChart.Series<>();
        completedSeries.setName("Ολοκληρωμένες");

        for (String m : allMonths) {
            long value = stats.completed.stream()
                    .filter(x -> x.getMonth().equals(m))
                    .map(MonthCountModel::getCount)
                    .findFirst()
                    .orElse(0L);

            completedSeries.getData().add(new XYChart.Data<>(m, value));
        }

        dualMaintenanceChart.getData().setAll(plannedSeries, completedSeries);

        // TOOLTIP για κάθε σημείο
        plannedSeries.getData().forEach(d ->
                Tooltip.install(d.getNode(),
                        new Tooltip("Προγραμματισμένες: " + d.getYValue()))
        );

        completedSeries.getData().forEach(d ->
                Tooltip.install(d.getNode(),
                        new Tooltip("Ολοκληρωμένες: " + d.getYValue()))
        );
    }






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

    private void styleChart(Chart chart) {
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        // remove background
        Node background = chart.lookup(".chart-plot-background");
        if (background != null) {
            background.setStyle("-fx-background-color: transparent;");
        }
    }
    private double computeRisk(OverallHealthModel health, long overdueMaint) {

        double offlineFactor = (double) health.getOfflineCount() / health.getTotalDevices();
        double overdueFactor = (double) overdueMaint / (health.getTotalDevices() * 2);

        // alerts weight
        //alertClient.getAllAlerts().size();
        double alertFactor = (double) alertClient.getAllAlerts().size() / 50.0;

        double risk = (offlineFactor * 0.5 + overdueFactor * 0.3 + alertFactor * 0.2) * 100;

        return Math.min(100, risk);
    }
    private void updateKpis() {
        MaintenanceStats stats = maintenanceClient.getMaintenanceStats();
        //μαι.setText(String.valueOf(stats.thisMonth));
        overdueLabel.setText(String.valueOf(stats.overdue));
        OverallHealthModel health = deviceClient.getFleetHealth();

        double riskPercent = computeRisk(health, stats.overdue);
        renderRiskGauge(riskPercent);
        riskLabel.setText((int)riskPercent + "%");

    }
    public void loadHeatmap() {
        List<FailureHeatmapCell> data = deviceClient.getFailureHeatmap();
        HeatmapComponent heatmap = new HeatmapComponent(data);
        heatmapOverlayPane.getChildren().setAll(heatmap);
    }

    private void setupKpiTooltips() {

        Tooltip.install(onlineDevicesBox, new Tooltip(
                "Συσκευές που απαντούν στο ping ή έχουν last_check < 2 λεπτά."
        ));

        Tooltip.install(offlineDevicesBox, new Tooltip(
                "Συσκευές που δεν απάντησαν στο ping για πάνω από 2 λεπτά."
        ));

        Tooltip.install(uptimeBox, new Tooltip(
                "Συνολικό ποσοστό χρόνου λειτουργίας όλων των συσκευών (Uptime%)."
        ));

        Tooltip.install(dueMaintBox, new Tooltip(
                "Πλήθος συντηρήσεων που ολοκληρώθηκαν τον τρέχοντα μήνα."
        ));

        Tooltip.install(overdueBox, new Tooltip(
                "Συντηρήσεις που έχουν περάσει την προγραμματισμένη ημερομηνία."
        ));

        Tooltip.install(riskBox, new Tooltip(
                "Risk Level = βάρος από outages + overdue + critical alerts.\n"
                        + "Υψηλή τιμή υποδηλώνει υψηλό επιχειρησιακό ρίσκο."
        ));
    }





}
