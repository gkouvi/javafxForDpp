package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.navigator.Navigation;
import javafx.fxml.FXML;

public class SidebarController {

    @FXML
    public void openDashboard() {
        Navigation.goTo("dashboard.fxml");
    }

    @FXML
    public void openSites() {
        Navigation.goTo("sites.fxml");
    }

    @FXML
    public void openBuildings() {
        Navigation.goTo("buildings.fxml");
    }

    @FXML
    public void openDevices() {
        Navigation.goTo("devices.fxml");
    }

    @FXML
    public void openAlerts() {
        Navigation.goTo("alerts.fxml");
    }

    @FXML
    public void openMaintenance() {
        Navigation.goTo("maintenance.fxml");
    }

    @FXML
    public void openDocuments() {
        Navigation.goTo("documents.fxml");
    }

    @FXML
    public void openSettings() {
        Navigation.goTo("main.fxml");
    }
}
