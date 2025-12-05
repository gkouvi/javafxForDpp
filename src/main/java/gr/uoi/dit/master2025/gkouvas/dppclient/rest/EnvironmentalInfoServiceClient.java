/*
package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.EnvironmentalInfoModel;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EnvironmentalInfoServiceClient extends ApiClient {

    public EnvironmentalInfoModel getByDevice(Long deviceId) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/environment/" + deviceId))
                    .GET()
                    .build();

            HttpResponse<String> res =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() != 200)
                return null;

            return mapper.readValue(res.body(), EnvironmentalInfoModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public EnvironmentalInfoModel save(EnvironmentalInfoModel env) {
        try {
            String json = mapper.writeValueAsString(env);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/environment"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> res =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() != 200)
                return null;

            return mapper.readValue(res.body(), EnvironmentalInfoModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
*/
package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.EnvironmentalInfoModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EnvironmentalInfoServiceClient extends ApiClient {

    // --------------------------
    // GET BY DEVICE
    // --------------------------

    public EnvironmentalInfoModel getByDevice(Long deviceId) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/environment/device/" + deviceId))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> resp =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            // 1️Αν ο server επέστρεψε 404 → ΔΕΝ ΥΠΑΡΧΟΥΝ ΔΕΔΟΜΕΝΑ → επιστρέφουμε empty model
            if (resp.statusCode() == 404) {
                return null;
            }

            // 2️Αν η απάντηση είναι κενή → new empty EnvironmentalInfoModel
            if (resp.body() == null || resp.body().isBlank()) {
                return null;
            }

            // 3️Κανονικό JSON → deserialize
            return mapper.readValue(resp.body(), EnvironmentalInfoModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    // --------------------------
    // SAVE / UPDATE
    // --------------------------
    public EnvironmentalInfoModel save(EnvironmentalInfoModel model) {
        try {

            String json = mapper.writeValueAsString(model);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/environment"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200 && resp.statusCode() != 201) {
                System.out.println("SAVE ENV ERROR: " + resp.statusCode());
                System.out.println(resp.body());
                return null;
            }

            return mapper.readValue(resp.body(), EnvironmentalInfoModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // -------------------------------------------------
    // ΓΙΑ ΤΟ CREATE DEVICE όπου θες μόνο ένα save()
    // -------------------------------------------------
    public void saveEnvironmentalInfo(EnvironmentalInfoModel model) {
        save(model); // απλό wrapper
    }
}
