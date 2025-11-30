package gr.uoi.dit.master2025.gkouvas.dppclient.controller;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceInterval;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.ModelForSiteAndBuilding;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EditDeviceController {

    public CheckBox hasIPCheck;
    public CheckBox dailyCheck;
    public CheckBox monthlyCheck;
    public CheckBox sixMonthCheck;
    public CheckBox yearlyCheck;
    public ComboBox<ModelForSiteAndBuilding> comboForBuildings;
    public TextField ipAddressField;
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private TextField serialField;
    @FXML private TextField firmwareField;
    @FXML private DatePicker dateField;
    @FXML private TextField statusField;

    private final DeviceServiceClient deviceClient = new DeviceServiceClient();
    private final SiteServiceClient siteClient = new SiteServiceClient();
    private final BuildingServiceClient buildingClient = new BuildingServiceClient();
    private final ObservableList<ModelForSiteAndBuilding> modelList = FXCollections.observableArrayList();
    private Long deviceId;



    public void loadDevice(DeviceModel d) {
        this.deviceId = d.getDeviceId();

        nameField.setText(d.getName());
        typeField.setText(d.getType());
        serialField.setText(d.getSerialNumber());
        firmwareField.setText(d.getFirmwareVersion());
        dateField.setValue(d.getInstallationDate());
        statusField.setText(d.getStatus());
        ipAddressField.setText(d.getIpAddress());
        hasIPCheck.setSelected(true);
        for(MaintenanceInterval interval :d.getMaintenanceIntervals()){
            switch(interval){
                case DAILY:dailyCheck.setSelected(true);break;
                case MONTHLY:monthlyCheck.setSelected(true);break;
                case ANNUAL:yearlyCheck.setSelected(true);break;
                case SEMI_ANNUAL:sixMonthCheck.setSelected(true);break;

            }

        }
        // 1. Γέμισμα ComboBox
        comboForBuildings.getItems().clear();

        for (var site : siteClient.getAllSites()) {
            for (var building : buildingClient.getAllBuildings()) {
                ModelForSiteAndBuilding model = new ModelForSiteAndBuilding();
                model.setSiteId(site.getId());
                model.setSiteName(site.getName());
                model.setBuildingId(building.getId());
                model.setBuildingName(building.getName());

                modelList.add(model);
            }


        }
        comboForBuildings.getItems().addAll(modelList);
        // 2. Προεπιλογή τρέχοντος κτιρίου
        if (d.getBuildingId() != null) {

            for (ModelForSiteAndBuilding b : modelList) {
                if (b.getBuildingId().equals(d.getBuildingId())) {
                    comboForBuildings.setValue(b);
                    //selectedBuildingId = b.getBuildingId();
                    break;
                }
            }
        }




    }

    @FXML
    public void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    public void onSave() {
        try {
            DeviceModel d = new DeviceModel();
            d.setDeviceId(deviceId);
            d.setName(nameField.getText());
            d.setType(typeField.getText());
            d.setSerialNumber(serialField.getText());
            d.setFirmwareVersion(firmwareField.getText());
            d.setInstallationDate(dateField.getValue());
            d.setStatus(statusField.getText());
            if(hasIPCheck.isSelected()){
                d.setIpAddress(ipAddressField.getText());

            }else{
                d.setIpAddress("Συσκευή χωρίς διεύθυνση IP");
            }
            d.setBuildingId(comboForBuildings.getValue().getBuildingId());
            List<MaintenanceInterval> maintenanceIntervals = new ArrayList<>();
            if(monthlyCheck.isSelected()){
                maintenanceIntervals.add(MaintenanceInterval.MONTHLY);
            }
            if(yearlyCheck.isSelected()){
                maintenanceIntervals.add(MaintenanceInterval.ANNUAL);
            }
            if(sixMonthCheck.isSelected()){
                maintenanceIntervals.add(MaintenanceInterval.SEMI_ANNUAL);
            }
            if(dailyCheck.isSelected()){
                maintenanceIntervals.add(MaintenanceInterval.DAILY);
            }
            d.setMaintenanceIntervals(maintenanceIntervals);


            deviceClient.updateDevice(deviceId, d);

            /*MainController.instance.refreshDevicesForBuilding(
                    MainController.SelectionContext.selectedBuildingId);*/

            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

