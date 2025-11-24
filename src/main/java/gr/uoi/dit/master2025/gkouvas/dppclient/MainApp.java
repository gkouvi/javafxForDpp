package gr.uoi.dit.master2025.gkouvas.dppclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/landing.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1024, 768);

        // 👉 Φόρτωση CSS
        scene.getStylesheets().add(
                getClass().getResource("/dashboard/haf-theme.css").toExternalForm()
        );
        /*scene.getStylesheets().add(
                getClass().getResource("/css/charts.css").toExternalForm()
        );*/


        stage.setTitle("DPP Client");
        stage.setScene(scene);
        stage.show();
    }

}
