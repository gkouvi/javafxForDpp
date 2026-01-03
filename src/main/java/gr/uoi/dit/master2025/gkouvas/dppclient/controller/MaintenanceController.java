package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.MaintenanceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.MaintenanceCategory;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.MaintenanceRules;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the Maintenance tab.
 * Shows maintenance logs and allows creating new logs.
 *
 * IMPORTANT:
 * - initialize(): runs only once (during FXML load)
 * - refresh(deviceId): called EVERY TIME user selects a new device
 */
public class MaintenanceController {

    @FXML private TableView<MaintenanceModel> maintenanceTable;

    @FXML private TableColumn<MaintenanceModel, Long> colId;
    @FXML private TableColumn<MaintenanceModel, LocalDate> colDate;
    @FXML private TableColumn<MaintenanceModel, String> colTech;
    @FXML private TableColumn<MaintenanceModel, String> colDesc;

    @FXML private Button addLogButton;

    private final MaintenanceServiceClient client = new MaintenanceServiceClient();
    private final ObservableList<MaintenanceModel> data = FXCollections.observableArrayList();


    /**
     * Runs ONLY once when FXML is initially created.
     * Sets up table columns & button handlers.
     */
    @FXML
    public void initialize() {

        // Column bindings
        colId.setCellValueFactory(val -> new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getLogId()));
        colDate.setCellValueFactory(val -> new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getMaintenanceDate()));
        colTech.setCellValueFactory(val -> new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getTechnician()));
        colDesc.setCellValueFactory(val -> new javafx.beans.property.SimpleObjectProperty<>(val.getValue().getDescription()));

        maintenanceTable.setItems(data);

        addLogButton.setOnAction(e -> showAddLogDialog());
        maintenanceTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(MaintenanceModel m, boolean empty) {
                super.updateItem(m, empty);

                if (m == null || empty) {
                    setStyle("");
                    return;
                }

                switch (m.getStatus()) {
                    case PENDING ->
                            setStyle("-fx-background-color: rgba(255,193,7,0.15);");
                    case AUTO_CLOSED ->
                            setStyle("-fx-background-color: rgba(76,175,80,0.15);");
                    case ESCALATED ->
                            setStyle("-fx-background-color: rgba(244,67,54,0.25);");
                    default ->
                            setStyle("");
                }
            }
        });


    }


    /**
     * Called by MainController EVERY TIME a new device is selected.
     * Clears table and reloads logs.
     */
    public void refresh(Long deviceId) {
        data.clear();
        if (deviceId != null) {
            loadLogs(deviceId);
        }
    }


    /**
     * Loads logs for the given device ID.
     */
    private void loadLogs(Long deviceId) {
        List<MaintenanceModel> logs = client.getMaintenanceByDevice(deviceId);
        data.setAll(logs);
    }


    /**
     * Add new maintenance entry dialog.
     */
    private void showAddLogDialog() {

        Dialog<MaintenanceModel> dialog = new Dialog<>();
        dialog.setTitle("Προσθήκη αρχείου καταγραφής συντήρησης");

        ButtonType saveBtn = new ButtonType("Αποθήκευση", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField techField = new TextField();
        TextArea descArea = new TextArea();
        descArea.setPrefRowCount(4);

        grid.add(new Label("Ημερομηνία:"), 0, 0);
        grid.add(datePicker, 1, 0);

        grid.add(new Label("Τεχνικός:"), 0, 1);
        grid.add(techField, 1, 1);

        grid.add(new Label("Περιγραφή:"), 0, 2);
        grid.add(descArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {

                MaintenanceModel log = new MaintenanceModel();
                log.setDeviceId(MainController.SelectionContext.selectedDeviceId);
                log.setMaintenanceDate(datePicker.getValue());
                log.setTechnician(techField.getText());
                log.setDescription(descArea.getText());

                return log;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(log -> {
            MaintenanceModel saved = client.createMaintenance(log);
            if (saved != null) {
                data.add(saved); // update table instantly
            }
        });
    }
    public void loadData(LocalDate date, MaintenanceCategory category) {

        List<MaintenanceModel> all = client.getAll();
        LocalDate today = LocalDate.now();

        List<MaintenanceModel> filtered =
                all.stream()
                        .filter(m -> MaintenanceRules.matches(m, date, category, today))
                        .toList();

        maintenanceTable.setItems(
                FXCollections.observableArrayList(filtered)
        );
    }



}
