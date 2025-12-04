package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceStats;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.time.temporal.IsoFields;


public class MaintenanceServiceClient extends ApiClient {

    private final ObjectMapper mapper = new ObjectMapper();

    public MaintenanceServiceClient() {
        mapper.registerModule(new JavaTimeModule());
    }

    public List<MaintenanceModel> getAll() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/maintenance/all"))
                    .GET()
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("MAINTENANCE JSON = " + res.body());
            return Arrays.asList(mapper.readValue(res.body(), MaintenanceModel[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<MaintenanceModel> getMaintenanceByDevice(Long deviceId) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/maintenance/device/" + deviceId))
                    .GET()
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(mapper.readValue(res.body(), MaintenanceModel[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public MaintenanceModel createMaintenance(MaintenanceModel model) {
        try {
            String body = mapper.writeValueAsString(model);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/maintenance"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() >= 200 && res.statusCode() < 300) {
                return mapper.readValue(res.body(), MaintenanceModel.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MaintenanceModel updateMaintenance(Long id, MaintenanceModel model) {
        try {
            String body = mapper.writeValueAsString(model);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/maintenance/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() >= 200 && res.statusCode() < 300) {
                return mapper.readValue(res.body(), MaintenanceModel.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MaintenanceModel completeMaintenance(Long id) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/maintenance/" + id + "/complete"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() >= 200 && res.statusCode() < 300) {
                return mapper.readValue(res.body(), MaintenanceModel.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Long> getUpcomingByMonth() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/maintenance/upcoming-months"))
                    .GET()
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() != 200) {
                System.out.println("UPCOMING MONTHS ERROR = " + res.body());
                return Map.of(); // empty map
            }

            // Deserialize JSON into Map<String, Long>
            return mapper.readValue(
                    res.body(),
                    mapper.getTypeFactory().constructMapType(Map.class, String.class, Long.class)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }

    public MaintenanceStats getMaintenanceStats() {
        try {
            List<MaintenanceModel> all = getAll();
            LocalDate now = LocalDate.now();

            long total = all.size();
            long thisMonth = 0;
            long thisWeek = 0;
            long today = 0;
            long overdue = 0;

            for (MaintenanceModel m : all) {

                LocalDate date = m.getMaintenanceDate();
                LocalDate planned = m.getPlannedDate();

                if (date != null) {
                    if (date.getMonth().equals(now.getMonth()) && date.getYear() == now.getYear())
                        thisMonth++;

                    if (date.get(IsoFields.WEEK_BASED_YEAR) == now.get(IsoFields.WEEK_BASED_YEAR))
                        thisWeek++;

                    if (date.equals(now))
                        today++;
                }

                if (planned != null && planned.isBefore(now))
                    overdue++;
            }

            return new MaintenanceStats(total, thisMonth, thisWeek, today, overdue);

        } catch (Exception e) {
            e.printStackTrace();
            return new MaintenanceStats();
        }
    }


}




