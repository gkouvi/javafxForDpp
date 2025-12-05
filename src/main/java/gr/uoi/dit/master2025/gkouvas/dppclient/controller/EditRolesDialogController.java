package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.RoleDto;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.UserRow;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class EditRolesDialogController {

    @FXML private Label usernameLabel;
    @FXML private CheckBox adminRole;
    @FXML private CheckBox supervisorRole;
    @FXML private CheckBox techRole;

    private UserRow user;
    private final ObjectMapper mapper = new ObjectMapper();

    public void setUser(UserRow user) {
        this.user = user;

        usernameLabel.setText("User: " + user.getUsername());

        // Προεπιλογή ρόλων
        Set<String> userRoles = new HashSet<>();
        user.getRoles().forEach(r -> userRoles.add(r.name()));

        adminRole.setSelected(userRoles.contains("ADMIN"));
        supervisorRole.setSelected(userRoles.contains("SUPERVISOR"));
        techRole.setSelected(userRoles.contains("TECHNICIAN"));
    }

    public boolean saveChanges() {
        try {
            Set<String> newRoles = new HashSet<>();

            if (adminRole.isSelected()) newRoles.add("ADMIN");
            if (supervisorRole.isSelected()) newRoles.add("SUPERVISOR");
            if (techRole.isSelected()) newRoles.add("TECHNICIAN");

            Map<String, Object> body = Map.of("roles", newRoles);

            String json = mapper.writeValueAsString(body);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(UserSession.getBaseUrl() + "/admin/users/" + user.getId() + "/roles"))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp =
                    HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());

            return resp.statusCode() == 200;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
