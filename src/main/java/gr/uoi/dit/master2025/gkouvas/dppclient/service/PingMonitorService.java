package gr.uoi.dit.master2025.gkouvas.dppclient.service;

import javafx.application.Platform;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.AlertServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.ui.DashboardPopup;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class PingMonitorService implements Runnable {

    private final List<DeviceModel> devices;
    private final AlertServiceClient alertClient = new AlertServiceClient();

    public PingMonitorService(List<DeviceModel> devices) {
        this.devices = devices;
    }

    @Override
    public void run() {
        while (true) {
            for (DeviceModel d : devices) {
                System.out.println(d.getName());
                boolean reachable = isReachable(d.getIpAddress(), 10000);

                if (!reachable && !d.isOffline()) {
                    d.setOffline(true);

                    // ---- CREATE ALERT: DOWN ----
                    alertClient.createPingAlert(
                            d.getDeviceId(),
                            "Συσκευή Offline",
                            "Η συσκευή δεν ανταποκρίθηκε στο ping"

                    );

                    Platform.runLater(() ->
                            DashboardPopup.show(
                                    "Συσκευή OFFLINE",
                                    d.getName() + " ΔΕΝ είναι προσβάσιμη!"
                            )
                    );

                } else if (reachable && d.isOffline()) {
                    d.setOffline(false);

                    // ---- CREATE ALERT: UP ----
                    alertClient.createPingAlert(
                            d.getDeviceId(),
                            "Συσκευή Online",
                            "Η συσκευή είναι και πάλι προσβάσιμη"
                    );

                    Platform.runLater(() ->
                            DashboardPopup.show(
                                    "Συσκευή ONLINE",
                                    d.getName() + " είναι και πάλι online"
                            )
                    );
                }
            }

            try {
                Thread.sleep(10000); // κάθε 10 sec
            } catch (InterruptedException ignored) {}
        }
    }

    private boolean isReachable(String ip, int timeoutMs) {
        try {
            return InetAddress.getByName(ip).isReachable(timeoutMs);
        } catch (IOException e) {
            return false;
        }
    }
}
