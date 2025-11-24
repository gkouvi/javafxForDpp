package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.SiteModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.TreeNodeData;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;




import java.net.URL;
import java.util.List;



public class MainController {
    //σε localhost, το QR από το κινητό ΔΕ ΘΑ ΛΕΙΤΟΥΡΓΕΙ.
    private static final String BASE_URL = "http://192.168.1.20:8080";
    ;
    @FXML private TreeView<TreeNodeData> treeView;

    @FXML private AnchorPane metadataPane;
    @FXML private AnchorPane alertsPane;
    @FXML private AnchorPane maintenancePane;
    @FXML private AnchorPane documentsPane;

    private final SiteServiceClient siteClient = new SiteServiceClient();
    private final BuildingServiceClient buildingClient = new BuildingServiceClient();
    private final DeviceServiceClient deviceClient = new DeviceServiceClient();

    // -----------------------------------------------------
    //  CONTROLLER INSTANCES (Tab Controllers Cache)
    // -----------------------------------------------------
    private MetadataController metadataController;
    private AlertsController alertsController;
    private MaintenanceController maintenanceController;
    private DocumentsController documentsController;


    // ---------------- SELECTED CONTEXT --------------------
    public static class SelectionContext {
        public static Long selectedSiteId = null;
        public static Long selectedBuildingId = null;
        public static Long selectedDeviceId = null;
    }
    public static MainController instance;


    @FXML
    public void initialize() {
        instance = this;
        loadTreeData();
        loadAllTabsOnce();     // HOT-LOAD όλα τα tabs μία φορά
        setupSelectionListener();
        setupContextMenu();
    }




    // ======================================================
    //           LOAD HIERARCHY (Sites → Buildings → Devices)
    // ======================================================
   /* private void loadTreeData() {

        TreeItem<TreeNodeData> root = new TreeItem<>(new TreeNodeData("root", 0L, "Sites"));
        root.setExpanded(true);

        List<SiteModel> sites = siteClient.getAllSites();

        for (SiteModel site : sites) {
            TreeItem<TreeNodeData> siteItem =
                    new TreeItem<>(new TreeNodeData("site", site.getId(), site.getName()));

            List<BuildingModel> buildings = buildingClient.getBuildingsBySite(site.getId());
            for (BuildingModel b : buildings) {

                TreeItem<TreeNodeData> buildingItem =
                        new TreeItem<>(new TreeNodeData("building", b.getId(), b.getName()));

                List<DeviceModel> devices = deviceClient.getDevicesByBuilding(b.getId());
                for (DeviceModel d : devices) {

                    TreeItem<TreeNodeData> deviceItem =
                            new TreeItem<>(new TreeNodeData("device", d.getDeviceId(), d.getName()));

                    buildingItem.getChildren().add(deviceItem);
                }

                siteItem.getChildren().add(buildingItem);
            }

            root.getChildren().add(siteItem);
        }

        treeView.setRoot(root);
    }*/
    private void loadTreeData() {

        // 1️ΟΛΙΚΗ ΚΑΘΑΡΙΣΗ TREEVIEW (critical fix)
        treeView.setRoot(null);
        treeView.setShowRoot(false);

        // 2️ΦΤΙΑΧΝΟΥΜΕ ΝΕΟ, ΚΑΘΑΡΟ ΔΕΝΤΡΟ
        TreeItem<TreeNodeData> root =
                new TreeItem<>(new TreeNodeData("root", 0L, "ΜΟΝΑΔΕΣ"));
        root.setExpanded(true);

        List<SiteModel> sites = siteClient.getAllSites();
        for (SiteModel site : sites) {

            TreeItem<TreeNodeData> siteItem = new TreeItem<>(
                    new TreeNodeData("site", site.getId(), site.getName()),
                    getIcon("site")
            );

            List<BuildingModel> buildings = buildingClient.getBuildingsBySite(site.getId());
            for (BuildingModel b : buildings) {

                TreeItem<TreeNodeData> buildingItem = new TreeItem<>(
                        new TreeNodeData("building", b.getId(), b.getName()),
                        getIcon("building")
                );

                List<DeviceModel> devices = deviceClient.getDevicesByBuilding(b.getId());
                for (DeviceModel d : devices) {
                    buildingItem.getChildren().add(
                            new TreeItem<>(
                                    new TreeNodeData("device", d.getDeviceId(), d.getName()),
                                    getIcon("device")
                            )
                    );
                }

                siteItem.getChildren().add(buildingItem);
            }

            root.getChildren().add(siteItem);
        }

        // 3 ΒΑΖΟΥΜΕ ΤΟ ΚΑΘΑΡΟ ROOT
        treeView.setRoot(root);
        treeView.setShowRoot(true);
    }






    // ======================================================
    //      LOAD ALL TABS ONCE (cache FXML + controllers)
    // ======================================================
    private void loadAllTabsOnce() {
        metadataController = (MetadataController)
                loadAndAttach(metadataPane, "/tabs/metadata.fxml");

        alertsController = (AlertsController)
                loadAndAttach(alertsPane, "/tabs/alerts.fxml");

        maintenanceController = (MaintenanceController)
                loadAndAttach(maintenancePane, "/tabs/maintenance.fxml");

        documentsController = (DocumentsController)
                loadAndAttach(documentsPane, "/tabs/documents.fxml");
    }


    /**
     * Loads an FXML exactly once, attaches it to its pane,
     * anchors it, and returns its controller.
     */
    private Object loadAndAttach(AnchorPane pane, String fxml) {
        try {
            URL url = getClass().getResource(fxml);
            FXMLLoader loader = new FXMLLoader(url);
            Node content = loader.load();

            pane.getChildren().add(content);
            AnchorPane.setTopAnchor(content, 0.0);
            AnchorPane.setBottomAnchor(content, 0.0);
            AnchorPane.setLeftAnchor(content, 0.0);
            AnchorPane.setRightAnchor(content, 0.0);

            return loader.getController();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load " + fxml);
        }
    }


    // ======================================================
    //                     SELECTION HANDLING
    // ======================================================
    private void setupSelectionListener() {
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, item) -> {

            if (item == null) return;

            TreeNodeData data = item.getValue();

            SelectionContext.selectedSiteId = null;
            SelectionContext.selectedBuildingId = null;
            SelectionContext.selectedDeviceId = null;

            switch (data.getType()) {

                case "site" -> {
                    SelectionContext.selectedSiteId = data.getId();
                    metadataController.refresh(SelectionContext.selectedSiteId, null, null);

                    // clear device-specific tabs
                    alertsController.refresh(null);
                    maintenanceController.refresh(null);
                    documentsController.refresh(null);
                }

                case "building" -> {
                    SelectionContext.selectedBuildingId = data.getId();
                    metadataController.refresh(null, SelectionContext.selectedBuildingId, null);

                    alertsController.refresh(null);
                    maintenanceController.refresh(null);
                    documentsController.refresh(null);
                }

                case "device" -> {
                    SelectionContext.selectedDeviceId = data.getId();
                    metadataController.refresh(null, null, SelectionContext.selectedDeviceId);

                    alertsController.refresh(SelectionContext.selectedDeviceId);
                    maintenanceController.refresh(SelectionContext.selectedDeviceId);
                    documentsController.refresh(SelectionContext.selectedDeviceId);
                }
            }
        });
    }




    private FontIcon getIcon(String type) {

        FontIcon icon = switch (type) {
            case "site"     -> new FontIcon(FontAwesome.MAP_MARKER);
            case "building" -> new FontIcon(FontAwesome.BUILDING);
            case "device"   -> new FontIcon(FontAwesome.MICROCHIP);
            default         -> new FontIcon(FontAwesome.QUESTION);
        };

        icon.setIconSize(18);
        icon.setIconColor(Color.web("#cbd5e1"));   // ανοιχτό γκρι για dark sidebar
        icon.getStyleClass().add("sidebar-icon");

        return icon;
    }

    public Image loadQR(String type, Long id) {

        String url = switch (type) {
            case "site" -> BASE_URL + "/qr/site/" + id;
            case "building" -> BASE_URL + "/qr/building/" + id;
            case "device" -> BASE_URL + "/qr/device/" + id;
            default -> null;
        };

        return new Image(url);
    }

    public static void selectDevice(Long id) {
        Platform.runLater(() -> instance.selectDeviceInTree(id));
    }

    public static void selectBuilding(Long id) {
        Platform.runLater(() -> instance.selectBuildingInTree(id));
    }




    private void selectNodeById(String type, Long id) {
        for (TreeItem<TreeNodeData> item : treeView.getRoot().getChildren()) {
            TreeNodeData data = item.getValue();
            if (data.getType().equals(type) && data.getId().equals(id)) {
                treeView.getSelectionModel().select(item);
                return;
            }
            selectNodeRecursive(item, type, id);
        }
    }

    private void selectNodeRecursive(TreeItem<TreeNodeData> parent, String type, Long id) {
        for (TreeItem<TreeNodeData> child : parent.getChildren()) {
            TreeNodeData d = child.getValue();
            if (d.getType().equals(type) && d.getId().equals(id)) {
                treeView.getSelectionModel().select(child);
                return;
            }
            selectNodeRecursive(child, type, id);
        }
    }

    private void expandAll(TreeItem<?> item) {
        if (item == null) return;
        item.setExpanded(true);
        item.getChildren().forEach(this::expandAll);
    }

    @FXML
    public void onAddDevice() {
        if (SelectionContext.selectedBuildingId == null) {
            showWarning("Παρακαλώ επιλέξτε πρώτα ένα κτίριο.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_device.fxml"));
            Parent root = loader.load();

            CreateDeviceController controller = loader.getController();
            controller.setBuildingId(SelectionContext.selectedBuildingId);

            Stage stage = new Stage();
            stage.setTitle("Προσθήκη συσκευής");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAddBuilding() {
        if (SelectionContext.selectedSiteId == null) {
            showWarning("Παρακαλώ επιλέξτε πρώτα έναν ιστότοπο.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_building.fxml"));
            Parent root = loader.load();

            CreateBuildingController controller = loader.getController();
            controller.setSiteId(SelectionContext.selectedSiteId);

            Stage stage = new Stage();
            stage.setTitle("Προσθήκη κτιρίου");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAddSite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_site.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Προσθήκη Μονάδας");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEditDevice() {

        Long id = SelectionContext.selectedDeviceId;
        if (id == null) {
            showWarning("Please select a device.");
            return;
        }

        try {
            DeviceModel d = deviceClient.getDevice(id);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_device.fxml"));
            Parent root = loader.load();

            EditDeviceController controller = loader.getController();
            controller.loadDevice(d);

            Stage stage = new Stage();
            stage.setTitle("Edit Device");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEditBuilding() {

        Long id = SelectionContext.selectedBuildingId;

        if (id == null) {
            showWarning("Παρακαλώ επιλέξτε ένα κτίριο.");
            return;
        }

        try {
            BuildingModel b = buildingClient.getBuilding(id);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_building.fxml"));
            Parent root = loader.load();

            EditBuildingController controller = loader.getController();
            controller.loadBuilding(b);

            Stage stage = new Stage();
            stage.setTitle("Επεξεργασία κτιρίου");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEditSite() {

        Long id = SelectionContext.selectedSiteId;

        if (id == null) {
            showWarning("Please select a site.");
            return;
        }

        try {
            SiteModel s = siteClient.getSite(id);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_site.fxml"));
            Parent root = loader.load();

            EditSiteController controller = loader.getController();
            controller.loadSite(s);

            Stage stage = new Stage();
            stage.setTitle("Edit Site");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDeleteDevice() {

        Long id = SelectionContext.selectedDeviceId;

        if (id == null) {
            showWarning("Παρακαλώ επιλέξτε μια συσκευή.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Επιβεβαίωση διαγραφής");
        confirm.setHeaderText("Διαγραφή συσκευής");
        confirm.setContentText("Είστε σίγουροι ότι θέλετε να διαγράψετε αυτή τη συσκευή;");

        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        deviceClient.deleteDevice(id);

        refreshDevicesForBuilding(SelectionContext.selectedBuildingId);
    }

    @FXML
    public void onDeleteBuilding() {

        Long id = SelectionContext.selectedBuildingId;

        if (id == null) {
            showWarning("Please select a building.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Επιβεβαίωση διαγραφής");
        confirm.setHeaderText("Διαγραφή κτιρίου");
        confirm.setContentText("Είστε σίγουροι ότι θέλετε να διαγράψετε αυτό το κτίριο;");

        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        buildingClient.deleteBuilding(id);

        refreshBuildingsForSite(SelectionContext.selectedSiteId);
    }
    @FXML
    public void onDeleteSite() {

        Long id = SelectionContext.selectedSiteId;

        if (id == null) {
            showWarning("Παρακαλώ επιλέξτε μια Μονάδα.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Επιβεβαίωση διαγραφής");
        confirm.setHeaderText("Διαγραφή Μονάδας");
        confirm.setContentText("Είστε σίγουροι ότι θέλετε να διαγράψετε αυτόν τον ιστότοπο;");

        if (confirm.showAndWait().get() != ButtonType.OK) {
            return;
        }

        siteClient.deleteSite(id);

        refreshSites();
    }

    private void setupContextMenu() {

        treeView.setCellFactory(tv -> {

            TreeCell<TreeNodeData> cell = new TreeCell<>() {

                @Override
                protected void updateItem(TreeNodeData item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setContextMenu(null);
                        return;
                    }

                    setText(item.getLabel());

                    // --- Context Menu ανάλογα με το type ---
                    ContextMenu menu = new ContextMenu();

                    switch (item.getType()) {

                        case "site" -> {
                            MenuItem edit = new MenuItem("Edit Site");
                            edit.setOnAction(e -> onEditSite());

                            MenuItem del = new MenuItem("Delete Site");
                            del.setOnAction(e -> onDeleteSite());

                            MenuItem addB = new MenuItem("Add Building");
                            addB.setOnAction(e -> onAddBuilding());

                            menu.getItems().addAll(edit, addB, del);
                        }

                        case "building" -> {
                            MenuItem edit = new MenuItem("Edit Building");
                            edit.setOnAction(e -> onEditBuilding());

                            MenuItem del = new MenuItem("Delete Building");
                            del.setOnAction(e -> onDeleteBuilding());

                            MenuItem addDev = new MenuItem("Add Device");
                            addDev.setOnAction(e -> onAddDevice());

                            menu.getItems().addAll(edit, addDev, del);
                        }

                        case "device" -> {
                            MenuItem edit = new MenuItem("Edit Device");
                            edit.setOnAction(e -> onEditDevice());

                            MenuItem del = new MenuItem("Delete Device");
                            del.setOnAction(e -> onDeleteDevice());

                           /* MenuItem exportQR = new MenuItem("Export QR");
                            exportQR.setOnAction(e -> onExportSelectedQR());

                            MenuItem printQR = new MenuItem("Print QR");
                            printQR.setOnAction(e -> onPrintSelectedQR());*/

                            menu.getItems().addAll(edit, del);
                        }
                    }

                    setContextMenu(menu);
                }
            };

            return cell;
        });
    }

    public void selectDeviceInTree(Long deviceId) {

        TreeItem<TreeNodeData> root = treeView.getRoot();

        for (TreeItem<TreeNodeData> siteItem : root.getChildren()) {
            for (TreeItem<TreeNodeData> buildingItem : siteItem.getChildren()) {
                for (TreeItem<TreeNodeData> deviceItem : buildingItem.getChildren()) {

                    if (deviceItem.getValue().getId().equals(deviceId) &&
                            deviceItem.getValue().getType().equals("device")) {

                        treeView.getSelectionModel().select(deviceItem);
                        treeView.scrollTo(treeView.getSelectionModel().getSelectedIndex());
                        return;
                    }
                }
            }
        }
    }

    public void selectBuildingInTree(Long buildingId) {

        TreeItem<TreeNodeData> root = treeView.getRoot();

        for (TreeItem<TreeNodeData> siteItem : root.getChildren()) {
            for (TreeItem<TreeNodeData> buildingItem : siteItem.getChildren()) {

                if (buildingItem.getValue().getId().equals(buildingId) &&
                        buildingItem.getValue().getType().equals("building")) {

                    treeView.getSelectionModel().select(buildingItem);
                    treeView.scrollTo(treeView.getSelectionModel().getSelectedIndex());
                    return;
                }
            }
        }
    }

    public void selectSiteInTree(Long siteId) {

        TreeItem<TreeNodeData> root = treeView.getRoot();

        for (TreeItem<TreeNodeData> siteItem : root.getChildren()) {

            if (siteItem.getValue().getId().equals(siteId) &&
                    siteItem.getValue().getType().equals("site")) {

                treeView.getSelectionModel().select(siteItem);
                treeView.scrollTo(treeView.getSelectionModel().getSelectedIndex());
                return;
            }
        }
    }

    public void handleScannedQR(String code) {

        if (code == null || code.isEmpty()) return;

        // ---- NEW FORMAT ----
        if (code.startsWith("DPP://device/")) {
            long id = Long.parseLong(code.substring("DPP://device/".length()));
            selectDeviceInTree(id);
            openDeviceCard(id);
            return;
        }

        // ---- OLD FORMAT (URL FORMAT) ----
        if (code.startsWith("http://localhost:8080/devices/qr/")) {
            long id = Long.parseLong(code.substring("http://localhost:8080/devices/qr/".length()));
            selectDeviceInTree(id);
            openDeviceCard(id);
            return;
        }

        // ---- BUILDING ----
        if (code.startsWith("DPP://building/")) {
            long id = Long.parseLong(code.substring("DPP://building/".length()));
            selectBuildingInTree(id);
            return;
        }

        // ---- SITE ----
        if (code.startsWith("DPP://site/")) {
            long id = Long.parseLong(code.substring("DPP://site/".length()));
            selectSiteInTree(id);
            return;
        }
    }
    @FXML
    public void onScanQrInMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dialogs/QRScannerDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("QR Scanner");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    /*private void onExportSelectedQR() {
        Long id = SelectionContext.selectedDeviceId;
        if (id == null) return;

        MetadataController.exportQR(id);
    }

    private void onPrintSelectedQR() {
        Long id = SelectionContext.selectedDeviceId;
        if (id == null) return;

        MetadataController.printQR(id);
    }*/

    public void openDeviceCard(Long deviceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/device-card.fxml"));
            Parent root = loader.load();

            DeviceCardController controller = loader.getController();
            controller.loadDevice(deviceId);

            Stage stage = new Stage();
            stage.setTitle("Device Details");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }












    public void refreshDevicesForBuilding(Long buildingId) {
        loadTreeData(); // reload full tree
    }
    public void refreshBuildingsForSite(Long siteId) {
        loadTreeData(); // reload whole tree
    }

    public void refreshSites() {
        loadTreeData();
    }


    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }







}
