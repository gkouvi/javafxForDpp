package gr.uoi.dit.master2025.gkouvas.dppclient.model;

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

    public String getDeviceName() {
        return deviceName;
    }

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
        if (deviceId != null) builder.append( "Συσκευή : " + deviceName + "/");
        if (buildingId != null) builder.append( "Κτίριο : " + buildingName);
        if(builder.isEmpty())
        return "-";
        else return builder.toString();
    }


}
