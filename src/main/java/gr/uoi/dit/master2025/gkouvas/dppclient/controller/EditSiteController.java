package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.SiteModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditSiteController {

    @FXML private TextField nameField;
    @FXML private TextField regionField;
    @FXML private TextField coordField;

    private final SiteServiceClient siteClient = new SiteServiceClient();
    private Long siteId;

    public void loadSite(SiteModel s) {
        this.siteId = s.getId();
        nameField.setText(s.getName());
        regionField.setText(s.getRegion());
        coordField.setText(s.getCoordinates());
    }

    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    public void onSave() {
        try {
            SiteModel s = new SiteModel();
            s.setId(siteId);
            s.setName(nameField.getText());
            s.setRegion(regionField.getText());
            s.setCoordinates(coordField.getText());

            siteClient.updateSite(siteId, s);

            // refresh tree completely
            MainController.instance.refreshSites();

            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
