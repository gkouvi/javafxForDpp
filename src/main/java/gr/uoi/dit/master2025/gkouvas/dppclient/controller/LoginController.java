package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.LoginResponse;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.SSLUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private void onLogin() {
        try {
            SSLUtil.disableCertificateValidation();

            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();




            URL url = new URL("https://192.168.0.105:8443/auth/login");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                LoginResponse response = mapper.readValue(conn.getInputStream(), LoginResponse.class);
                UserSession.setToken(response.token());
                UserSession.setUsername(username);
                closeWindow();
            } else {
                errorLabel.setVisible(true);
                errorLabel.setText("Invalid credentials.");
            }

        } catch (Exception e) {
            errorLabel.setVisible(true);
            errorLabel.setText("Connection error.");
            e.printStackTrace();
        }
    }


    private void closeWindow() {
        ((Stage) usernameField.getScene().getWindow()).close();
    }


}

