package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.ui.HeatmapComponent;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.MaintenanceCategory;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.MaintenanceRules;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DashboardContentController {


    public CategoryAxis alertsXAxis;
    public NumberAxis alertsYAxis;

    public Pane heatmapOverlayPane;

    public VBox overdueBox;
    public LineChart dualMaintenanceChart;
    public NumberAxis leftYAxis;
    public Label pendingLabel;
    public Label completedLabel;
    public Label cancelledLabel;
    public CategoryAxis maintenanceXAxis;
    public StackPane maintenanceChartContainer;
    public Label autoClosedLabel;
    public Label escalatedLabel;


    //public StackPane heatmapContainer;
    @FXML private Canvas heatmapCanvas;
       // ===== CHARTS =====
    @FXML private BarChart<String, Number> alertsChart;
    @FXML private BarChart<String, Number> maintenanceBarChart;


    @FXML public StackPane riskGaugePane;

    @FXML private Label lblPendingAlerts;
    @FXML private Label lblMaintenanceInProgress;
    @FXML private Label lblMttr;
    @FXML private Label lblLastIotEventTime;
    @FXML private Label lblLastIotEventDetails;

    @FXML private ListView<String> riskDriversList;

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
    public Label totalMaintLabel;
    public VBox riskBox;
    public Label riskLabel;
    @FXML private Label overdueLabel;
    public VBox onlineDevicesBox;
    public VBox offlineDevicesBox;
    public VBox uptimeBox;
    public Label offlineDevicesLabel;
    public Label uptimeLabel;
    OverallHealthModel health;
    private final KpiClient kpiClient = new KpiClient();




    // -----------------------------------------
    // INITIALIZE – καλείται μόλις φορτωθεί το FXML
    // -----------------------------------------

    @FXML
    public void initialize() {

        // 1) Fleet health (backend)
        health = deviceClient.getFleetHealth();
        loadHealthKpis();

        // 2) KPI από backend ΜΟΝΟ (θα ενημερώσει labels + risk gauge)
        updateKpis();

        // 3) Charts / widgets
        loadAlertsChart();
        styleChart(alertsChart);

        loadHeatmap();

        setupKpiTooltips();
        loadActiveIssues();
        loadRiskDrivers();
        loadLastIotEventFromAlerts();

        // 4) Maintenance chart
        if (maintenanceBarChart != null) {
            maintenanceBarChart.getStylesheets().add(
                    getClass().getResource("/css/charts.css").toExternalForm()
            );
            Platform.runLater(() -> {
                loadMaintenanceChart();
            });




            addHoverEffect(maintenanceBarChart);
            addHoverEffect(alertsChart);
            addHoverEffect(riskGaugePane);
            addHoverEffect(heatmapOverlayPane);
            addHoverEffect(uptimeBox);
            addHoverEffect(offlineDevicesBox);
            addHoverEffect(dueMaintBox);
            addHoverEffect(overdueBox);
            addHoverEffect(riskBox);
        }
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
                (riskPercent < 20) ? Color.DARKGREEN :
                (riskPercent < 40) ? Color.LIMEGREEN :
                (riskPercent < 60) ? Color.GOLD :
                (riskPercent < 80) ? Color.ORANGE :
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
    // MAINTENANCE
    // -----------------------------------------





   private void loadMaintenanceChart() {

       List<MaintenanceModel> all = maintenanceClient.getAll();
       if (all == null || all.isEmpty()) {
           maintenanceBarChart.getData().clear();
           return;
       }

       LocalDate today = LocalDate.now();

       // ⏱️ Χρονικό παράθυρο: -30 → +30
       List<LocalDate> timeline =
               IntStream.rangeClosed(-30, 30)
                       .mapToObj(today::plusDays)
                       .toList();

       // ---------------- AXIS ----------------
       CategoryAxis xAxis = (CategoryAxis) maintenanceBarChart.getXAxis();
       xAxis.setTickLabelRotation(45);
       xAxis.setGapStartAndEnd(false);
       xAxis.setCategories(
               FXCollections.observableArrayList(
                       timeline.stream()
                               .map(LocalDate::toString)
                               .toList()
               )
       );

       maintenanceBarChart.setAnimated(false);
       maintenanceBarChart.setCategoryGap(6);
       maintenanceBarChart.setBarGap(2);

       // ---------------- SERIES ----------------
       XYChart.Series<String, Number> plannedSeries        = new XYChart.Series<>();
       XYChart.Series<String, Number> completedSeries      = new XYChart.Series<>();
       XYChart.Series<String, Number> pendingFutureSeries  = new XYChart.Series<>();
       XYChart.Series<String, Number> overdueSeries        = new XYChart.Series<>();

       plannedSeries.setName("Προγραμματισμένες");
       completedSeries.setName("Υλοποιημένες");
       pendingFutureSeries.setName("Εκκρεμείς / Μελλοντικές");
       overdueSeries.setName("Εκπρόθεσμες");

       // ---------------- POPULATE ----------------
       for (LocalDate d : timeline) {

           String key = d.toString();

           // 1  Ό,τι είχε προγραμματιστεί (baseline)
           long plannedCount = all.stream()
                   .filter(m -> m.getPlannedDate() != null)
                   .filter(m -> m.getPlannedDate().equals(d))
                   .filter(m -> m.getStatus() != MaintenanceStatus.CANCELLED)
                   .count();

           // 2 Ό,τι ολοκληρώθηκε εκείνη την ημέρα
           long completedCount = all.stream()
                   .filter(m -> m.getStatus() == MaintenanceStatus.COMPLETED)
                   .filter(m -> d.equals(m.getPerformedDate()))
                   .count();

           // 3 Εκκρεμείς με ημερομηνία στο μέλλον ή σήμερα
           long pendingFutureCount = all.stream()
                   .filter(m -> m.getStatus() == MaintenanceStatus.PENDING)
                   .filter(m -> m.getPlannedDate() != null)
                   .filter(m -> m.getPlannedDate().equals(d))
                   .filter(m -> !m.getPlannedDate().isBefore(today))
                   .count();

           // 4 Εκπρόθεσμες
           long overdueCount = all.stream()
                   .filter(m -> m.getStatus() == MaintenanceStatus.PENDING)
                   .filter(m -> m.getPlannedDate() != null)
                   .filter(m -> m.getPlannedDate().equals(d))
                   .filter(m -> m.getPlannedDate().isBefore(today))
                   .count();

           // ---------------- DATA ----------------
           XYChart.Data<String, Number> pl = new XYChart.Data<>(key, plannedCount);
           XYChart.Data<String, Number> c  = new XYChart.Data<>(key, completedCount);
           XYChart.Data<String, Number> p  = new XYChart.Data<>(key, pendingFutureCount);
           XYChart.Data<String, Number> o  = new XYChart.Data<>(key, overdueCount);

           plannedSeries.getData().add(pl);
           completedSeries.getData().add(c);
           pendingFutureSeries.getData().add(p);
           overdueSeries.getData().add(o);

           // ---------------- CLICKS ----------------
           addClickIfAny(pl, plannedCount, d, MaintenanceCategory.PLANNED);
           addClickIfAny(c,  completedCount, d, MaintenanceCategory.COMPLETED);
           addClickIfAny(p,  pendingFutureCount, d, MaintenanceCategory.PENDING);
           addClickIfAny(o,  overdueCount, d, MaintenanceCategory.OVERDUE);
       }

       // ---------------- RENDER ----------------
       maintenanceBarChart.getData().setAll(
               plannedSeries,
               completedSeries,
               pendingFutureSeries,
               overdueSeries
       );

       maintenanceBarChart.applyCss();
       maintenanceBarChart.layout();
   }


    private void addClickIfAny(
            XYChart.Data<String, Number> data,
            long count,
            LocalDate date,
            MaintenanceCategory category
    ) {
        if (count <= 0) return;

        addBarClick(data, date, category);
    }




    private void addBarClick(
            XYChart.Data<String, Number> data,
            LocalDate date,
            MaintenanceCategory category
    ) {
        data.nodeProperty().addListener((obs, oldNode, node) -> {
            if (node == null) return;

            node.setCursor(Cursor.HAND);
            node.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    openMaintenanceList(date, category);
                }
            });
        });
    }






    private void openMaintenanceList(
            LocalDate date,
            MaintenanceCategory category
    ) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/tabs/maintenance.fxml")
            );

            Parent root = loader.load();
            MaintenanceController ctrl = loader.getController();

            // περνάμε ΜΟΝΟ context (όχι business logic)
            ctrl.loadData(date, category);

            Stage stage = new Stage();
            stage.setTitle(
                    "Συντηρήσεις " + date + " (" + categoryToLabel(category) + ")"
            );

            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private String categoryToLabel(MaintenanceCategory c) {
        return switch (c) {
            case PLANNED   -> "Προγραμματισμένες";
            case COMPLETED -> "Υλοποιημένες";
            case PENDING   -> "Εκκρεμείς";
            case OVERDUE   -> "Εκπρόθεσμες";
        };
    }

    private void loadAlertsChart() {

        List<AlertModel> alerts = new ArrayList<>();



        // μαζεύουμε ΟΛΕΣ τις συσκευές και όλα τα alerts
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
    private double computeOperationalRisk(OverallHealthModel health, long overdueMaint) {
        if (health.getTotalDevices() == 0) return 0.0;//θωράκιση απο διαίρεση με το 0
        //Αν 1–2 συσκευές πέσουν → το risk ανεβαίνει απότομα

        double offlineRatio =
                (double) health.getOfflineCount() / health.getTotalDevices();

// Logistic-like amplification
        double offlineFactor = 1 - Math.exp(-3 * offlineRatio);

        double overdueRatio =
                (double) overdueMaint / health.getTotalDevices();

        double overdueFactor = 1 - Math.exp(-2 * overdueRatio);


        // alerts weight Alert factor με severity
        List<AlertModel> alerts = alertClient.getAllAlerts();


        long weightedAlerts = alerts.stream()
                .mapToLong(a -> switch (a.getSeverity()) {
                    case AlertSeverity.CRITICAL -> 5;
                    case AlertSeverity.HIGH    -> 3;
                    case AlertSeverity.MEDIUM   -> 2;
                    default         -> 1;
                })
                .sum();

        double alertFactor = Math.min(1.0, weightedAlerts / (health.getTotalDevices() * 5.0));


        double heatmapFactor = computeHeatmapRisk(
                deviceClient.getFailureHeatmap()
        );
        heatmapFactor = Math.min(1.0, Math.max(0.0, heatmapFactor));

        double rawRisk =
                offlineFactor  * 0.450 +
                        overdueFactor  * 0.10 +
                        alertFactor    * 0.20 +
                        heatmapFactor  * 0.25;

// Επιχειρησιακή ανοχή 15%
        double adjustedRisk = Math.max(0, rawRisk - 0.15);

        return Math.min(100, adjustedRisk * 100);



    }

    private double computeHeatmapRisk(List<FailureHeatmapCell> data) {

        if (data == null || data.isEmpty()) {
            return 0.0;
        }

        int totalAlerts = data.stream()
                .mapToInt(FailureHeatmapCell::getCount)
                .sum();

        if (totalAlerts == 0) {
            return 0.0;
        }

        int peak = data.stream()
                .mapToInt(FailureHeatmapCell::getCount)
                .max()
                .orElse(0);

        double normalizedLoad =
                Math.min(1.0, totalAlerts / 50.0);

        double concentration =
                (double) peak / totalAlerts;

        return Math.min(1.0,
                0.6 * normalizedLoad +//0.6
                        0.4 * concentration//0.4
        );
    }

    /*private double computeHeatmapRisk(List<FailureHeatmapCell> data) {

        if (data == null || data.isEmpty()) {
            return 0.0;
        }

        int totalAlerts = data.stream()
                .mapToInt(FailureHeatmapCell::getCount)
                .sum();

        int peak = data.stream()
                .mapToInt(FailureHeatmapCell::getCount)
                .max()
                .orElse(0);

        // Πόσα κελιά έχουν alerts
        long activeCells = data.stream()
                .filter(c -> c.getCount() > 0)
                .count();

        // Συγκέντρωση: λίγα κελιά με πολλά alerts = κακό
        double concentration =
                (double) peak / Math.max(1, totalAlerts);

        // Κανονικοποίηση (εμπειρικά ασφαλής)
        double normalizedLoad =
                Math.min(1.0, totalAlerts / 50.0);

        // Τελικός heatmap factor
        return Math.min(1.0,
                0.6 * normalizedLoad +
                        0.4 * concentration
        );
    }*/

    private void updateKpis() {

        MaintenanceBoxesKpiModel kpi = maintenanceClient.getMaintenanceKpis();

        if (kpi == null) {
            totalMaintLabel.setText("-");
            completedLabel.setText("-");
            pendingLabel.setText("-");
            overdueLabel.setText("-");
            riskLabel.setText("-");
            cancelledLabel.setText("-");
            return;
        }

        totalMaintLabel.setText(String.valueOf(kpi.getTotal()));
        completedLabel.setText(String.valueOf(kpi.getCompleted()));
        pendingLabel.setText(String.valueOf(kpi.getPending()));
        overdueLabel.setText(String.valueOf(kpi.getOverdue()));
        cancelledLabel.setText(String.valueOf(kpi.getCancel()));
        autoClosedLabel.setText(
                String.valueOf(kpi.getAutoClosed())
        );

        escalatedLabel.setText(
                String.valueOf(kpi.getEscalated())
        );



        OverallHealthModel h = (health != null) ? health : deviceClient.getFleetHealth();

        double riskPercent = computeOperationalRisk(h, kpi.getOverdue());
        renderRiskGauge(riskPercent);
        riskLabel.setText((int) riskPercent + "%");
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
                "Ο συνολικός δείκτης Risk εκφράζει το λειτουργικό ρίσκο του συστήματος.\n\n"
                        + "Υπολογίζεται από:\n"
                        + "• Διαθεσιμότητα συσκευών (offline)\n"
                        + "• Εκπρόθεσμες συντηρήσεις\n"
                        + "• Πλήθος και σοβαρότητα ειδοποιήσεων\n"
                        + "• Χρονική συγκέντρωση βλαβών (heatmap)\n\n"
                        + "Υψηλές τιμές υποδηλώνουν αυξημένη επιχειρησιακή πίεση\n"
                        + "και ανάγκη άμεσης προτεραιοποίησης ενεργειών."
        ));

    }


    private void loadActiveIssues() {
        //Εκκρεμείς ειδοποιήσεις
        List<AlertModel> alerts = alertClient.getAllAlerts();

        long pendingAlerts = alerts.stream()
                .filter(a -> !"CLOSED".equalsIgnoreCase(a.getStatus().name()))
                .count();

        lblPendingAlerts.setText(String.valueOf(pendingAlerts));
       // Συντηρήσεις σε εξέλιξη
        List<MaintenanceModel> maints = maintenanceClient.getAll();

        long inProgress = maints.stream()
                .filter(m -> "PENDING".equalsIgnoreCase(m.getStatus().name()))
                .count();


        lblMaintenanceInProgress.setText(String.valueOf(inProgress));
        loadMttr();



    }
    private void loadLastIotEventFromAlerts() {

        List<AlertModel> alerts = alertClient.getAllAlerts();

        alerts.stream()
                .max(Comparator.comparing(AlertModel::getCreatedAt))
                .ifPresentOrElse(alert -> {

                    lblLastIotEventTime.setText(
                            alert.getCreatedAt().toString()
                    );

                    lblLastIotEventDetails.setText(
                            alert.getDeviceName() +
                                    " / κρισιμότητα = " + alert.getSeverity() +
                                    " / κατάσταση = " + alert.getStatus()
                    );

                }, () -> {
                    lblLastIotEventTime.setText("—");
                    lblLastIotEventDetails.setText("—");
                });
    }

    private void loadRiskDrivers() {

        ObservableList<String> drivers = FXCollections.observableArrayList();

        long offline = computeOfflineDevices();
        if (offline > 0) {
            drivers.add("🔴 " + offline + " offline συσκευές (μειωμένη διαθεσιμότητα)");
        }

        long overdue = computeOverdueMaintenance();
        if (overdue > 0) {
            drivers.add("🛠 " + overdue + " εκπρόθεσμες συντηρήσεις (αυξημένο τεχνικό χρέος)");
        }

        long criticalAlerts = alertClient.getAllAlerts().stream()
                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL)
                .count();

        if (criticalAlerts > 0) {
            drivers.add("⚠ " + criticalAlerts + " critical alerts (επιχειρησιακή πίεση)");
        }

        // 🟠 ΝΕΟ — Heatmap contribution
        double heatmapFactor = computeHeatmapRisk(
                deviceClient.getFailureHeatmap()
        );

        if (heatmapFactor > 0.25) {
            drivers.add("🟠 Χρονική συγκέντρωση βλαβών (heatmap peak)");
        }

        if (drivers.isEmpty()) {
            drivers.add("✅ Δεν εντοπίστηκαν παράγοντες κινδύνου");
        }

        riskDriversList.setItems(drivers);
    }

    private long computeOfflineDevices() {

        List<DeviceModel> devices = deviceClient.getAllDevices();

        return devices.stream()
                .filter(d -> Boolean.TRUE.equals(d.isOffline()))//!Boolean.TRUE.equals(d.isOffline())
                .count();
    }
    private long computeOverdueMaintenance() {
        LocalDate today = LocalDate.now();
        List<MaintenanceModel> maints = maintenanceClient.getAll();

        return maints.stream()
                .filter(m -> MaintenanceRules.isOverdue(m, today))
                .count();
    }

    private void addHoverEffect(Node node) {
        node.setOnMouseEntered(e ->
                node.setStyle("-fx-opacity: 1.0; -fx-scale-x:1.05; -fx-scale-y:1.05;"));

        node.setOnMouseExited(e ->
                node.setStyle("-fx-opacity: 0.85; -fx-scale-x:1.0; -fx-scale-y:1.0;"));
    }
    private void loadMttr() {

        double mttr = kpiClient.getOperationalMttr();


        if (mttr > 0) {
            lblMttr.setText(mttr + " ημέρες");
        } else {
            lblMttr.setText("—");
        }
    }





}
