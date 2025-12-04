package gr.uoi.dit.master2025.gkouvas.dppclient.service;

import javafx.application.Platform;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.AlertServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.ui.DashboardPopup;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PingMonitorService implements Runnable {

    private final List<DeviceModel> devices;
    private final AlertServiceClient alertClient = new AlertServiceClient();

    // τελευταία γνωστή κατάσταση per device (true = reachable, false = unreachable)
    private final Map<Long, Boolean> lastReachable = new ConcurrentHashMap<>();

    /*public PingMonitorService(List<DeviceModel> devices) {
        this.devices = devices;
        // αρχικοποίηση map (χωρίς alerts στο ξεκίνημα)
        for (DeviceModel d : devices) {
            if (d.getDeviceId() != null) {
                lastReachable.put(d.getDeviceId(), null); // άγνωστη αρχική κατάσταση
            }
        }
    }*/
    public PingMonitorService(List<DeviceModel> devices) {
        this.devices = devices;

        for (DeviceModel d : devices) {
            Long id = d.getDeviceId();

            if (id == null) {
               continue; // avoid crash
            }

            lastReachable.put(id, Boolean.FALSE); // άγνωστη αρχική κατάσταση
        }
    }


    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (DeviceModel d : devices) {

                Long id = d.getDeviceId();
                String ip = d.getIpAddress();

                // skip αν δεν υπάρχει IP
                if (id == null || ip == null || ip.isBlank()) {
                    continue;
                }

                boolean reachable = isReachable(ip, 3000); // 3 sec timeout

                Boolean last = lastReachable.get(id);

                // 1η φορά για αυτή τη συσκευή → απλά αποθηκεύω την κατάσταση, ΔΕΝ στέλνω alert
                if (last == null) {
                    lastReachable.put(id, reachable);
                    d.setOffline(!reachable);
                    continue;
                }

                // transition: ONLINE -> OFFLINE
                if (!reachable && last) {
                    lastReachable.put(id, false);
                    d.setOffline(true);

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
                }

                // transition: OFFLINE -> ONLINE
                else if (reachable && !last) {
                    lastReachable.put(id, true);
                    d.setOffline(false);

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
                // αν δεν άλλαξε η κατάσταση, απλά ενημερώνουμε τον χάρτη
                else {
                    lastReachable.put(id, reachable);
                    d.setOffline(!reachable);
                }
            }

            try {
                Thread.sleep(10_000); // κάθε 10 sec
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
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
