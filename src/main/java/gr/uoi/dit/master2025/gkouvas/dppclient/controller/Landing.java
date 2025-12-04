package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class Landing {

    public Button builidingBtn;
    public Button dashboardBtn;
    public Button sitesBtn;
    public Button devicesBtn;
    public Button alertsBtn;
    public Button maintnanceBtn;
    public Button documentBtn;
    public Button envBtn;
    public Button settingsBtn;
    public Button qrBtn;
    @FXML private BorderPane rootPane;
    public static Landing instance;
    @FXML private AnchorPane contentArea;
    private Button activeButton = null;

    @FXML
    public void initialize() {
        instance = this;   // 🔥 Κρατάμε το instance
        setActive(dashboardBtn);
        loadView("/dashboard/dashboardContent.fxml");
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
                    getClass().getResource("/dashboard/dashboardContent.fxml")
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
        setActive(dashboardBtn);
        loadView("/dashboard/dashboardContent.fxml");

    }

    @FXML private void onSitesClick() {
        setActive(sitesBtn);
        loadView("/fxml/sites.fxml");

    }

    @FXML private void onBuildingsClick() {
        setActive(builidingBtn);
        loadView("/fxml/buildings.fxml");
    }

    @FXML private void onDevicesClick() {
        setActive(devicesBtn);
        loadView("/fxml/devices.fxml");
    }

    @FXML private void onAlertsClick() {
        setActive(alertsBtn);
        loadView("/fxml/alerts.fxml");
    }

    @FXML
    private void onMaintenanceClick() {
        setActive(maintnanceBtn);
        loadView("/fxml/maintenance.fxml");
    }

    @FXML private void onDocumentsClick() {
        setActive(documentBtn);
        loadView("/fxml/documents.fxml");
    }

    @FXML private void onSettingsClick() {

        setActive(settingsBtn);loadView("/fxml/main.fxml");
    }

    public void onQrcode(ActionEvent actionEvent) {
        setActive(qrBtn);
        loadView("/dialogs/QRScannerDialog.fxml");
    }
    @FXML
    public void onEnvironmentClick(ActionEvent actionEvent) {
        setActive(envBtn);
        loadView("/fxml/environment.fxml");
    }



    private void setActive(Button btn) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("sidebar-btn-active");
        }

        btn.getStyleClass().add("sidebar-btn-active");
        activeButton = btn;
    }

}

