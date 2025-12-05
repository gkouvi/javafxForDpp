package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.UserRow;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.SSLUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;

public class DisableUserDialogController {

    @FXML private Label lblUsername;
    @FXML private Label lblStatus;
    @FXML private Label statusMessage;
    @FXML private Button btnToggle;

    private UserRow user; // from caller
    private final ObjectMapper mapper = new ObjectMapper();

    public void setUser(UserRow user) {
        this.user = user;

        lblUsername.setText(user.getUsername());
        lblStatus.setText(user.isEnabled() ? "Enabled" : "Disabled");

        if (user.isEnabled()) {
            btnToggle.setText("Disable User");
            btnToggle.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        } else {
            btnToggle.setText("Enable User");
            btnToggle.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
        }
    }

    @FXML
    private void onToggle() {
        try {
            String endpoint,body;
            SSLUtil.disableCertificateValidation();

            if(!user.isEnabled()) {
                System.out.println(user.isEnabled());
                endpoint = "https://192.168.0.105:8443/admin/users/" + user.getId() + "/enable";



                body = "{\"enabled\":" + (user.isEnabled()) + "}";
            }else{
                endpoint = "https://192.168.0.105:8443/admin/" + user.getId() + "/disable";



                body = "{\"enabled\":" + (!user.isEnabled()) + "}";
            }

            URL url = new URL(endpoint);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + UserSession.getToken());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int code = conn.getResponseCode();

            if (code == 200) {
                user.setEnabled(!user.isEnabled());
                lblStatus.setText(user.isEnabled() ? "Ενεργοποιημένος" : "Απενεργοποιημένος");

                statusMessage.setVisible(true);
                statusMessage.setStyle("-fx-text-fill: green;");
                statusMessage.setText("User updated successfully.");

                // update button
                setUser(user);

            } else {
                statusMessage.setVisible(true);
                statusMessage.setStyle("-fx-text-fill: red;");
                statusMessage.setText("Αποτυχία: " + code);
            }

        } catch (Exception e) {
            statusMessage.setVisible(true);
            statusMessage.setText("Σφάλμα κατά την ενημέρωση του χρήστη.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onClose() {
        ((Stage) lblUsername.getScene().getWindow()).close();
    }
}
