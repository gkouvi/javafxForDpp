package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.UserRow;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ResetPasswordDialogController {

    @FXML private Label usernameLabel;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private UserRow user;
    private final ObjectMapper mapper = new ObjectMapper();

    public void setUser(UserRow user) {
        this.user = user;
        usernameLabel.setText("Reset password for: " + user.getUsername());
    }

    public boolean resetPassword() {
        try {
            String newPass = newPasswordField.getText().trim();
            String confirm = confirmPasswordField.getText().trim();

            // Validation
            if (newPass.isEmpty() || confirm.isEmpty()) {
                errorLabel.setText("All fields are required.");
                errorLabel.setVisible(true);
                return false;
            }

            if (!newPass.equals(confirm)) {
                errorLabel.setText("Passwords do not match.");
                errorLabel.setVisible(true);
                return false;
            }

            Map<String, String> body = Map.of("newPassword", newPass);
            String json = mapper.writeValueAsString(body);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(UserSession.getBaseUrl() + "/admin/users/" + user.getId() + "/reset-password"))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp =
                    HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());

            return resp.statusCode() == 200;

        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Connection error.");
            errorLabel.setVisible(true);
            return false;
        }
    }
}
