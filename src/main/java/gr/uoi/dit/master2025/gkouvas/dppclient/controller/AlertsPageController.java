package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.AlertServiceClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AlertsPageController {

    @FXML private TableView<AlertModel> alertsTable;


    @FXML private TableColumn<AlertModel, Long> deviceColumn;
    @FXML private TableColumn<AlertModel, String> messageColumn;
    @FXML private TableColumn<AlertModel, String> timestampColumn;

    private final AlertServiceClient alertClient = new AlertServiceClient();


    @FXML
    public void initialize() {

        //idColumn.setCellValueFactory(new PropertyValueFactory<>("alertId"));
        deviceColumn.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
alertClient.getAllAlerts().stream().peek(System.out::println);
        alertsTable.setItems(FXCollections
                .observableArrayList(alertClient.getAllAlerts()));
        setupContextMenu();
    }


    private void setupContextMenu() {

        alertsTable.setRowFactory(tv -> {
            TableRow<AlertModel> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();

            MenuItem edit = new MenuItem("Επεξεργασία Ειδοποίησης");


            edit.setOnAction(e -> onEditAlert(row.getItem()));


            menu.getItems().addAll(edit );

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            return row;
        });



    }
    private void onEditAlert(AlertModel row) {
        if (row == null) return;

        AlertModel d = alertClient.getAllertFromDeviceID((row.getDeviceId()));
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit-alert.fxml"));
            Parent root = loader.load();

            EditAlertController controller = loader.getController();
            controller.loadAlert(d);

            Stage stage = new Stage();
            stage.setTitle("Επεξεργασία Ειδοποίησης");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
