package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.EnvironmentalInfoModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.EnvironmentalInfoServiceClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EnvironmentalInfoDialogController {


    public Label envScoreLabel;
    @FXML private TextArea materialsArea;
    @FXML private TextArea recyclingArea;
    @FXML private TextArea hazardousArea;
    @FXML private TextField recyclabilityField;
    @FXML private TextField weightField;

    private final EnvironmentalInfoServiceClient envClient = new EnvironmentalInfoServiceClient();
    private Long deviceId;
    private EnvironmentalInfoModel model;
    private boolean saved = false;

    public boolean isSaved() { return saved; }


    public void setDeviceId(Long id) {
        this.deviceId = id;

        if (id != null) {
            loadInfo();
        } else {
            // CREATE mode → κενό μοντέλο
            this.model = new EnvironmentalInfoModel();
        }

    }

    private void loadInfo() {

        model = envClient.getByDevice(deviceId);

        if (model == null) {
            model = new EnvironmentalInfoModel();

            model.setDeviceId(deviceId);
        }

        materialsArea.setText(nullSafe(model.getMaterialsComposition()));
        recyclingArea.setText(nullSafe(model.getRecyclingInstructions()));
        hazardousArea.setText(nullSafe(model.getHazardousMaterials()));
        recyclabilityField.setText(
                model.getRecyclabilityPercentage() == null ? "" : model.getRecyclabilityPercentage().toString()
        );
        weightField.setText(
                model.getDeviceWeightKg() == null ? "" : model.getDeviceWeightKg().toString()
        );

        int score = model.computeEnvironmentalScore();
        envScoreLabel.setText(score + "/100");

        envScoreLabel.getStyleClass().removeAll(
                "env-score-green", "env-score-yellow", "env-score-red"
        );

        if (score >= 70) envScoreLabel.getStyleClass().add("env-score-green");
        else if (score >= 40) envScoreLabel.getStyleClass().add("env-score-yellow");
        else envScoreLabel.getStyleClass().add("env-score-red");

        Tooltip tp = buildEnvironmentalTooltip(model, score);
        Tooltip.install(envScoreLabel, tp);

    }





    @FXML
    private void onSave() {
        try {
            if (model == null) {
                model = new EnvironmentalInfoModel();
            }

            model.setMaterialsComposition(materialsArea.getText());
            model.setRecyclingInstructions(recyclingArea.getText());
            model.setHazardousMaterials(hazardousArea.getText());

            if (!recyclabilityField.getText().isBlank())
                model.setRecyclabilityPercentage(Double.parseDouble(recyclabilityField.getText()));

            if (!weightField.getText().isBlank())
                model.setDeviceWeightKg(Double.parseDouble(weightField.getText()));

            // ΣΕ CREATE MODE δεν έχουμε deviceId → δεν κάνουμε backend save
            if (deviceId != null) {
                model.setDeviceId(deviceId);
                model = envClient.save(model);
            }

            saved = true;
            close();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Σφάλμα", "Λάθος δεδομένα:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        this.saved=false;
        close();
    }

    private void close() {
        Stage stage = (Stage) materialsArea.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    private String nullSafe(String v) {
        return v == null ? "" : v;
    }

    public void refresh(Long deviceId) {
        this.deviceId = deviceId;
        loadInfo();
    }

    public void setInfo(EnvironmentalInfoModel temp) {
        if (temp == null) return;

        this.model = temp;

        materialsArea.setText(nullSafe(temp.getMaterialsComposition()));
        recyclingArea.setText(nullSafe(temp.getRecyclingInstructions()));
        hazardousArea.setText(nullSafe(temp.getHazardousMaterials()));
        recyclabilityField.setText(
                temp.getRecyclabilityPercentage() == null ? "" :
                        temp.getRecyclabilityPercentage().toString()
        );
        weightField.setText(
                temp.getDeviceWeightKg() == null ? "" :
                        temp.getDeviceWeightKg().toString()
        );
    }

    public EnvironmentalInfoModel getInfo() {


        return model;
    }
    private Tooltip buildEnvironmentalTooltip(EnvironmentalInfoModel info, int score) {

        double recyclability = info.getRecyclabilityPercentage() != null
                ? info.getRecyclabilityPercentage()
                : 0;

        double weight = info.getDeviceWeightKg() != null
                ? info.getDeviceWeightKg()
                : 0;

        String hazards = info.getHazardousMaterials() != null
                ? info.getHazardousMaterials()
                : "—";

        // Recompute detailed subscores
        double weightScore =
                weight < 1 ? 100 :
                        weight < 3 ? 70 :
                                weight < 10 ? 40 :
                                        10;

        double hazardScore =
                hazards.isBlank() ? 100 :
                        hazards.toLowerCase().contains("pb") ||
                                hazards.toLowerCase().contains("hg") ||
                                hazards.toLowerCase().contains("cr6")
                                ? 20 : 60;

        String text =
                "♻ Ανακυκλωσιμότητα: " + recyclability + "%\n" +
                        "⚖ Βάρος: " + weight + " kg → score: " + (int)weightScore + "\n" +
                        "☣ Επικίνδυνα υλικά: " + hazards + " → score: " + (int)hazardScore + "\n\n" +
                        "📊 Τελικό περιβαλλοντικό σκορ: " + score + "/100";

        Tooltip tp = new Tooltip(text);
        tp.setStyle("-fx-font-size: 14px; -fx-font-weight: normal;");

        return tp;
    }

}
