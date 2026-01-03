package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.AlertModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.PageResponse;
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

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("getAlertsForDevice failed: " + response.body());
                return List.of();
            }

            String body = response.body().trim();

            if (body.startsWith("[")) {
                return mapper.readValue(
                        body,
                        new TypeReference<List<AlertModel>>() {}
                );
            }

            PageResponse<AlertModel> page =
                    mapper.readValue(
                            body,
                            new TypeReference<PageResponse<AlertModel>>() {}
                    );

            return page.getContent() != null ? page.getContent() : List.of();

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

            if (response.statusCode() != 200) {
                System.err.println("getAllAlerts failed: " + response.body());
                return List.of();
            }

            String body = response.body().trim();

            // 🔹 CASE 1: JSON ARRAY
            if (body.startsWith("[")) {
                return mapper.readValue(
                        body,
                        new TypeReference<List<AlertModel>>() {}
                );
            }

            // 🔹 CASE 2: JSON OBJECT (Page)
            PageResponse<AlertModel> page =
                    mapper.readValue(
                            body,
                            new TypeReference<PageResponse<AlertModel>>() {}
                    );

            return page.getContent() != null ? page.getContent() : List.of();

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

            // 🔒 ΑΜΥΝΤΙΚΟΣ ΕΛΕΓΧΟΣ
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.err.println("updateAlert failed (" +
                        response.statusCode() + "): " + response.body());
                return null;
            }

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


}

