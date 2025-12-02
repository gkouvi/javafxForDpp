package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.EnvironmentalInfoModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.EnvironmentalInfoServiceClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class EnvironmentController {

    @FXML private TableView<EnvRow> envTable;

    @FXML private TableColumn<EnvRow, String> colDeviceName;
    @FXML private TableColumn<EnvRow, String> colType;
    @FXML private TableColumn<EnvRow, String> colRecyclability;
    @FXML private TableColumn<EnvRow, String> colHazard;
    @FXML private TableColumn<EnvRow, String> colScore;
    @FXML private TableColumn<EnvRow, String> colEdit;

    private final DeviceServiceClient deviceClient = new DeviceServiceClient();
    private final EnvironmentalInfoServiceClient envClient = new EnvironmentalInfoServiceClient();

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
        setupRowClick();
        setupEditColumn();

    }

    private void setupColumns() {
        colDeviceName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().deviceName()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().type()));
        colRecyclability.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().recyclability()));
        colHazard.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().hazard()));
        colScore.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ecoScore()));
    }

    private void loadData() {
        envTable.getItems().clear();

        List<DeviceModel> devices = deviceClient.getAllDevices();

        for (DeviceModel d : devices) {
            EnvironmentalInfoModel info = envClient.getByDevice(d.getDeviceId());

            EnvRow row = new EnvRow(
                    d.getDeviceId(),
                    d.getName(),
                    d.getType(),
                    info == null ? "-" : percent(info.getRecyclabilityPercentage()),
                    info == null ? "-" : safe(info.getHazardousMaterials()),
                    computeScore(info)
            );

            envTable.getItems().add(row);
        }
    }

    private void setupRowClick() {
        envTable.setRowFactory(tv -> {
            TableRow<EnvRow> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    EnvRow item = row.getItem();
                    openDeviceCard(item.deviceId());
                }
            });

            return row;
        });
    }

    private void openDeviceCard(Long deviceId) {
        try {
            DashboardController dc = new DashboardController();
            dc.openDeviceCard(deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- HELPERS ----------

    private String safe(String s) { return s == null ? "-" : s; }

    private String percent(Double d) { return d == null ? "-" : String.format("%.0f%%", d); }

    private String computeScore(EnvironmentalInfoModel info) {
        if (info == null) return "—";

        int score = 0;

        if (info.getRecyclabilityPercentage() != null)
            score += Math.min(info.getRecyclabilityPercentage(), 100);

        if (info.getHazardousMaterials() != null && !info.getHazardousMaterials().isBlank())
            score -= 20;

        if (score < 0) score = 0;

        return score + "/100";
    }

    // Record row structure
    public record EnvRow(Long deviceId, String deviceName, String type,
                         String recyclability, String hazard, String ecoScore) {}

    private void setupEditColumn() {
        colEdit.setCellFactory(col -> new TableCell<>() {

            private final Button btn = new Button("Επεξεργασία");

            {
                btn.getStyleClass().add("action-btn"); // αν έχεις CSS class
                btn.setOnAction(e -> {
                    EnvRow row = getTableView().getItems().get(getIndex());
                    openEditEnvironmentalDialog(row.deviceId());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }
    private void openEditEnvironmentalDialog(Long deviceId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/EnvironmentalInfoDialog.fxml")
            );

            Parent root = loader.load();
            EnvironmentalInfoDialogController ctrl = loader.getController();

            ctrl.setDeviceId(deviceId);

            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("Επεξεργασία περιβαλλοντικών");
            st.setScene(new Scene(root));
            st.showAndWait();

            // Refresh after dialog closes
            loadData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
