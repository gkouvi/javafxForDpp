package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditBuildingController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField siteIdField;

    private final BuildingServiceClient buildingClient = new BuildingServiceClient();
    private Long buildingId;

    // Φορτώνουμε τα υπάρχοντα δεδομένα
    public void loadBuilding(BuildingModel b) {

        this.buildingId = b.getId();

        nameField.setText(b.getName());
        addressField.setText(b.getAddress());
        siteIdField.setText(b.getSiteId() != null ? b.getSiteId().toString() : "");
    }

    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    public void onSave() {
        try {
            BuildingModel b = new BuildingModel();
            b.setId(buildingId);
            b.setName(nameField.getText());
            b.setAddress(addressField.getText());
            b.setSiteId(Long.parseLong(siteIdField.getText()));

            buildingClient.updateBuilding(buildingId, b);

            MainController.instance.refreshBuildingsForSite(
                    MainController.SelectionContext.selectedSiteId);

            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
