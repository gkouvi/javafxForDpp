package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.*;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.MultipartUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

/**
 * REST client for interacting with /devices endpoints.
 */
public class DeviceServiceClient extends ApiClient {

    public List<DeviceModel> getAllDevices() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(response.body(), DeviceModel[].class));

        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }

    public DeviceModel getDevice(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices/" + id))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {

                return null;
            }

            try {
                return mapper.readValue(response.body(), DeviceModel.class);
            } catch (Exception ex) {

                ex.printStackTrace();
                return null;
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public List<DeviceModel> getDevicesByBuilding(Long buildingId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices/building/" + buildingId))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(
                    mapper.readValue(response.body(), DeviceModel[].class)
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }

    public void uploadDeviceQr(Long deviceId, BufferedImage qr) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qr, "png", baos);
            byte[] png = baos.toByteArray();

            String boundary = "----DPPQRUPLOAD1234";

            byte[] multipart = MultipartUtil.buildMultipart(png, boundary, "file", "qr.png");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices/" + deviceId + "/qr"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipart))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public DeviceModel createDevice(DeviceModel device) {
        try {
            String body = mapper.writeValueAsString(device);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), DeviceModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateDevice(Long deviceId, DeviceModel device) {
        try {
            String json = mapper.writeValueAsString(device);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices/" + deviceId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDevice(Long deviceId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices/" + deviceId))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .DELETE()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DeviceModel> getUpcomingMaintenance() {
        try {
            String endpoint = BASE_URL + "/devices/upcoming-maintenance";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {

                return List.of();
            }

            // Deserialize JSON array → List<DeviceModel>
            DeviceModel[] arr = mapper.readValue(response.body(), DeviceModel[].class);
            return Arrays.asList(arr);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<UpcomingMaintenanceItem> getUpcomingMaintenanceDetails() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices/upcoming-maintenance/details"))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(
                    response.body(), UpcomingMaintenanceItem[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    public MaintenanceKpiModel getMaintenanceKpis() {
        try {
            String url = BASE_URL + "/devices/maintenance-kpis";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), MaintenanceKpiModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return new MaintenanceKpiModel();
        }
    }

    public OverallHealthModel getFleetHealth() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices/health"))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(res.body(), OverallHealthModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return new OverallHealthModel();
        }
    }


    public List<FailureHeatmapCell> getFailureHeatmap() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/analytics/heatmap"))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(res.body(), FailureHeatmapCell[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }


}











