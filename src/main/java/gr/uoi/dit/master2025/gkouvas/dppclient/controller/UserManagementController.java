package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.UserRow;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class UserManagementController {
    private final ObjectMapper mapper = new ObjectMapper();
    public TableColumn<UserRow,String> colId;
    public TableColumn<UserRow,String> colUsername;
    public TableColumn<UserRow,String> colRoles;
    public TableColumn<UserRow,String> colEnabled;
    @FXML
    private TableView<UserRow> usersTable;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEnabled.setCellValueFactory(new PropertyValueFactory<>("enabled"));

        // Για εμφάνιση ρόλων ως κείμενο
        colRoles.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRolesAsString())
        );
        loadUsers();
    }

    private void loadUsers() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://192.168.0.105:8443/admin/users"))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> resp =
                    HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());

            UserRow[] rows = mapper.readValue(resp.body(), UserRow[].class);
            usersTable.getItems().setAll(rows);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onAddUser() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddUserDialog.fxml"));
            DialogPane pane = loader.load();

            AddUserDialogController ctrl = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Δημιουργία χρήστη");

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (ctrl.createUser()) {
                    loadUsers();   // refresh table after creation
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @FXML
    private void onEditRoles() {
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Παρακαλώ επιλέξτε πρώτα έναν χρήστη.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditRolesDialog.fxml"));
            DialogPane pane = loader.load();

            EditRolesDialogController ctrl = loader.getController();
            ctrl.setUser(selected);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Επεξεργασία ρόλων");

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (ctrl.saveChanges()) {
                    loadUsers();  // refresh table
                } else {
                    showError("Αποτυχία ενημέρωσης ρόλων.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }


    @FXML
    private void onResetPassword() {

        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Παρακαλώ επιλέξτε πρώτα έναν χρήστη.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResetPasswordDialog.fxml"));
            DialogPane pane = loader.load();

            ResetPasswordDialogController ctrl = loader.getController();
            ctrl.setUser(selected);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Επαναφορά κωδικού πρόσβασης");

            var result = dialog.showAndWait();

            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (ctrl.resetPassword()) {
                    showInfo("Ο κωδικός πρόσβασης ενημερώθηκε με επιτυχία.");
                } else {
                    showError("Αποτυχία ενημέρωσης κωδικού πρόσβασης.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onDisableUser() {
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DisableUserDialog.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DisableUserDialogController ctrl = loader.getController();
        ctrl.setUser(selected);

        Stage stage = new Stage();
        stage.setTitle("Απενεργοποίηση χρήστη");
        stage.setScene(new Scene(root));
        stage.showAndWait();

        loadUsers(); // refresh table
    }



    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}

