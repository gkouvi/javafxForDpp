package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import java.time.LocalDate;

public class UpcomingMaintenanceItem {
    private Long deviceId;
    private String deviceName;
    private MaintenanceInterval interval;
    private LocalDate nextMaintenanceDate;
    // getters & setters


    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public MaintenanceInterval getInterval() {
        return interval;
    }

    public void setInterval(MaintenanceInterval interval) {
        this.interval = interval;
    }

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
}

