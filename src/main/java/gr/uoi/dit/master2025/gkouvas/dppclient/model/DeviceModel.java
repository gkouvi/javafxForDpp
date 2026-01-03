
package gr.uoi.dit.master2025.gkouvas.dppclient.model;


import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * UI Model for representing a Device installed in a Building.
 */
public class DeviceModel {

    private Long deviceId;
    private String name;
    private String type;
    private String serialNumber;
    private LocalDate installationDate;
    private String firmwareVersion;
    private String status;
    private Long buildingId;

    private String qrBase64;
    private String ipAddress;
    private boolean offline = false;
    private boolean online;
    private Double uptimePercent;
    public LocalDateTime lastCheck;
    private String bimElementId;

    private String bimDiscipline;

    public String getBimElementId() {
        return bimElementId;
    }

    public void setBimElementId(String bimElementId) {
        this.bimElementId = bimElementId;
    }

    public String getBimDiscipline() {
        return bimDiscipline;
    }

    public void setBimDiscipline(String bimDiscipline) {
        this.bimDiscipline = bimDiscipline;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(LocalDateTime lastCheck) {
        this.lastCheck = lastCheck;
    }

    public Double getUptimePercent() {
        return uptimePercent;
    }

    public void setUptimePercent(Double uptimePercent) {
        this.uptimePercent = uptimePercent;
    }

    public boolean isOnline() {
        return !offline;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;

    private String buildingName;

    // ✔ NEW MULTI-MAINTENANCE SUPPORT
    private List<MaintenanceInterval> maintenanceIntervals = new ArrayList<>();

    public DeviceModel() {}

    // ----------- BASIC FIELDS -----------

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    // ----------- BUILDING HELPER -----------
    public String getBuildingName() {
        BuildingModel bmodel = new BuildingServiceClient().getBuilding(buildingId);
        return bmodel.getName(); }
    public void setBuildingName(String b) { this.buildingName = b;}

        // ----------- QR CODE -----------

    public String getQrBase64() {
        return qrBase64;
    }

    public void setQrBase64(String qrBase64) {
        this.qrBase64 = qrBase64;
    }

    // ----------- IP / OFFLINE -----------

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    // ----------- MAINTENANCE DATES -----------

    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    // ----------- MULTI-MAINTENANCE -----------

    public List<MaintenanceInterval> getMaintenanceIntervals() {
        return maintenanceIntervals;
    }

    public void setMaintenanceIntervals(List<MaintenanceInterval> maintenanceIntervals) {
        this.maintenanceIntervals = maintenanceIntervals;
    }

    // Convenience helper (optional)
    public boolean hasInterval(MaintenanceInterval interval) {
        return maintenanceIntervals != null && maintenanceIntervals.contains(interval);
    }
}
