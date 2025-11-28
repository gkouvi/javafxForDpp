package gr.uoi.dit.master2025.gkouvas.dppclient.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DashboardPopup {

    public static void show(String title, String message) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);

        Label label = new Label(title + "\n" + message);
        label.setStyle(
                "-fx-font-size:16px; -fx-padding:15;" +
                        "-fx-background-color:#0a2342;" +
                        "-fx-text-fill:white;" +
                        "-fx-border-color:#1ec3ff; -fx-border-width:2;" +
                        "-fx-background-radius:10; -fx-border-radius:10;"
        );

        StackPane root = new StackPane(label);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        FadeTransition fade = new FadeTransition(Duration.seconds(8), root);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> stage.close());
        fade.play();
    }
}
