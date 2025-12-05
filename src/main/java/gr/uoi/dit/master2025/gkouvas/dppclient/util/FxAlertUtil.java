package gr.uoi.dit.master2025.gkouvas.dppclient.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class FxAlertUtil {

    public static void showError(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Σφάλμα");
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    public static void showPermissionDenied() {
        showError("❌ Δεν έχετε δικαιώματα πρόσβασης για αυτή τη λειτουργία.");
    }
}
