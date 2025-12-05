package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class AddUserDialogController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminRole;
    @FXML private CheckBox supervisorRole;
    @FXML private CheckBox techRole;
    @FXML private Label errorLabel;

    private final ObjectMapper mapper = new ObjectMapper();

    public boolean createUser() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Απαιτείται όνομα χρήστη και κωδικός πρόσβασης.");
                errorLabel.setVisible(true);
                return false;
            }

            Set<String> roles = new HashSet<>();
            if (adminRole.isSelected()) roles.add("ADMIN");
            if (supervisorRole.isSelected()) roles.add("SUPERVISOR");
            if (techRole.isSelected()) roles.add("TECHNICIAN");

            if (roles.isEmpty()) {
                errorLabel.setText("Επιλέξτε τουλάχιστον έναν ρόλο.");
                errorLabel.setVisible(true);
                return false;
            }

            Map<String, Object> body = Map.of(
                    "username", username,
                    "password", password,
                    "roles", roles
            );

            String json = mapper.writeValueAsString(body);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(UserSession.getBaseUrl() + "/admin/users/create"))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp =
                    HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                return true;
            } else {
                errorLabel.setText("Server error: " + resp.statusCode());
                errorLabel.setVisible(true);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Connection error.");
            errorLabel.setVisible(true);
            return false;
        }
    }
}
