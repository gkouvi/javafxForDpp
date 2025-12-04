package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import java.util.List;

public class OverallHealthModel {
    public long totalDevices;
    public long onlineCount;
    public long offlineCount;
    public double fleetUptimePercent;
    private List<DeviceModel> devices;

    public long getTotalDevices() {
        return totalDevices;
    }

    public void setTotalDevices(long totalDevices) {
        this.totalDevices = totalDevices;
    }

    public long getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(long onlineCount) {
        this.onlineCount = onlineCount;
    }

    public long getOfflineCount() {
        return offlineCount;
    }

    public void setOfflineCount(long offlineCount) {
        this.offlineCount = offlineCount;
    }

    public double getFleetUptimePercent() {
        return fleetUptimePercent;
    }

    public void setFleetUptimePercent(double fleetUptimePercent) {
        this.fleetUptimePercent = fleetUptimePercent;
    }

    public List<DeviceModel> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceModel> devices) {
        this.devices = devices;
    }
}

