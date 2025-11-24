package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceModel;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class MaintenanceServiceClient extends ApiClient {

    public List<MaintenanceModel> getMaintenanceByDevice(Long deviceId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/maintenance/device/" + deviceId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(response.body(), MaintenanceModel[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public MaintenanceModel createMaintenance(MaintenanceModel log) {
        try {
            String json = mapper.writeValueAsString(log);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/maintenance"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), MaintenanceModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<MaintenanceModel> getAll() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/maintenance/all"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(response.body(), MaintenanceModel[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

}


