package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
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
                    .header("Authorization", "Bearer " + UserSession.getToken())
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
                    .header("Authorization", "Bearer " + UserSession.getToken())
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
                    .header("Authorization", "Bearer " + UserSession.getToken())
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
                    .header("Authorization", "Bearer " + UserSession.getToken())
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
                    .header("Authorization", "Bearer " + UserSession.getToken())
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
//δεν απαιτείται
    public void createPingAlert(Long deviceId, String type, String message) {
        try {

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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

