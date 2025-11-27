package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertModel;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class AlertServiceClient extends ApiClient {

    public List<AlertModel> getAlertsForDevice(Long deviceId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/alerts/device/" + deviceId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(response.body(), AlertModel[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public AlertModel createAlert(AlertModel alert) {
        try {
            String json = mapper.writeValueAsString(alert);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/alerts"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), AlertModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<AlertModel> getAllAlerts() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/alerts"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(response.body(), AlertModel[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }


    public AlertModel updateAlert(AlertModel alert) {
        try {
            String json = mapper.writeValueAsString(alert);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/alerts/" + alert.getAlertId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), AlertModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ========================= GET ALERT BY DEVICE ============================
    public AlertModel getAllertFromDeviceID(Long deviceId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/alerts/device/" + deviceId))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            AlertModel[] list = mapper.readValue(response.body(), AlertModel[].class);

            return list.length > 0 ? list[0] : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createPingAlert(Long deviceId, String type, String message) {
        try {
            System.out.println(type);
            AlertModel model = new AlertModel();
            model.setDeviceId(deviceId);
            model.setStatus(type);
            model.setMessage(message);
            model.setDueDate(LocalDate.now());

            String json = mapper.writeValueAsString(model);


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/alerts/ping"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Ping Alert Created: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

