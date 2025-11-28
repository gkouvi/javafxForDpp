package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;

import java.time.LocalDate;

/**
 * UI Model for representing a maintenance log entry for a device.
 */
public class MaintenanceModel {

    private Long logId;
    private Long deviceId;
    private LocalDate maintenanceDate;
    private String description;
    private String technician;
    private Long  buildingId;
    private transient  String deviceName;
    private transient String buildingName;
    private final BuildingServiceClient buildingClient = new BuildingServiceClient();
    private final DeviceServiceClient deviceClient = new DeviceServiceClient();

    private MaintenanceInterval interval;

   /* public BuildingServiceClient getBuildingClient() {
        return buildingClient;
    }

    public DeviceServiceClient getDeviceClient() {
        return deviceClient;
    }*/

    public MaintenanceInterval getInterval() {
        return interval;
    }

    public void setInterval(MaintenanceInterval interval) {
        this.interval = interval;
    }

    /*public String getDeviceName() {
        if (deviceClient.getDevice(deviceId)!= null) {
            this.setDeviceName(
                    deviceClient.getDevice(deviceId).getName()
            );
        }

        return deviceName;
    }*/

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Long  getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long  buildingId) {
        this.buildingId = buildingId;
    }

    public MaintenanceModel() {}

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnician() {
        return technician;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public String getTargetName() {
        StringBuilder builder = new StringBuilder();
        if (buildingId != null) builder.append( buildingClient.getBuilding(buildingId).getName()+" / ");
        if (deviceId != null) builder.append( deviceClient.getDevice(deviceId).getName() );

        if(builder.isEmpty())
        return "-";
        else return builder.toString();
    }


}
