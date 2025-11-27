package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;

import java.time.LocalDate;

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
    private String ipAddress;  // ΝΕΟ
    private boolean offline = false;
    private MaintenanceInterval maintenanceInterval;

    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    private LocalDate lastMaintenanceDate;//
    private LocalDate nextMaintenanceDate;// ΝΕΟ// ΝΕΟ

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

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

    public String getQrBase64() {
        return qrBase64;
    }

    public void setQrBase64(String qrBase64) {
        this.qrBase64 = qrBase64;
    }


    public DeviceModel() {}

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

    public String getBuildingName() {

        return new BuildingServiceClient().getBuilding(buildingId).getName();
    }

    public MaintenanceInterval getMaintenanceInterval() {
        return maintenanceInterval;
    }

    public void setMaintenanceInterval(MaintenanceInterval maintenanceInterval) {
        this.maintenanceInterval = maintenanceInterval;
    }
}
