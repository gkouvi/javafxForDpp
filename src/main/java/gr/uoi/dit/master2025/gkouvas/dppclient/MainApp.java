package gr.uoi.dit.master2025.gkouvas.dppclient;

import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        showLogin(stage);
        if (!UserSession.isLoggedIn()) {

            Platform.exit();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/landing.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1024, 768);

        // 👉 Φόρτωση CSS
        scene.getStylesheets().add(
                getClass().getResource("/dashboard/haf-theme.css").toExternalForm()
        );



        stage.setTitle("Εφαρμογή DPP");
        stage.setScene(scene);
        stage.show();
    }

    private void showLogin(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();

        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));
        loginStage.setResizable(false);
        loginStage.showAndWait();


    }
}