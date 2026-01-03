package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KpiClient extends ApiClient {

    private static final String BASE_URL_local = "/api/kpi";

    public double getOperationalMttr() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + BASE_URL_local + "/mttr"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Double.parseDouble(response.body());

        } catch (Exception e) {
            return 0.0;
        }
    }
}

