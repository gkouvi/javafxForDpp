package gr.uoi.dit.master2025.gkouvas.dppclient.navigator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Navigation {

    public static void goTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(Navigation.class.getResource("/fxml/" + fxml));
            Stage stage = (Stage) StageHolder.primaryStage.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
