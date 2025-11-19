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
}
