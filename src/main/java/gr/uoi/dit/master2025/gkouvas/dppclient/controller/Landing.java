package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class Landing {

    @FXML private BorderPane rootPane;
    public static Landing instance;
    @FXML private AnchorPane contentArea;

    @FXML
    public void initialize() {
        instance = this;   // 🔥 Κρατάμε το instance
        loadView("/fxml/dashboard.fxml");
    }

    // ---------------- ΦΟΡΤΩΣΤΕ ΟΠΟΙΟΔΗΠΟΤΕ FXML ΣΤΟ ΚΕΝΤΡΟ ----------------
    private void loadView(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlName)
            );
            AnchorPane pane = loader.load();

            contentArea.getChildren().setAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/dashboard.fxml")
            );
            AnchorPane pane = loader.load();

            contentArea.getChildren().setAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ---------------- SIDEBAR BUTTON ACTIONS ----------------

    @FXML private void onDashboardClick() {
        loadView("/fxml/dashboard.fxml");
    }

    @FXML private void onSitesClick() {
        loadView("/fxml/sites.fxml");
    }

    @FXML private void onBuildingsClick() {
        loadView("/fxml/buildings.fxml");
    }

    @FXML private void onDevicesClick() {
        loadView("/fxml/devices.fxml");
    }

    @FXML private void onAlertsClick() {
        loadView("/fxml/alerts.fxml");
    }

    @FXML
    private void onMaintenanceClick() {
        loadView("/fxml/maintenance.fxml");
    }

    @FXML private void onDocumentsClick() {
        loadView("/fxml/documents.fxml");
    }

    @FXML private void onSettingsClick() {
        loadView("/fxml/main.fxml");
    }

    public void onQrcode(ActionEvent actionEvent) {
        loadView("/dialogs/QRScannerDialog.fxml");
    }
}

