package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DeviceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.util.MultipartUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
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
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), DeviceModel.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /*public List<DeviceModel> getDevicesByBuilding(Long buildingId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices?buildingId=" + buildingId))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(response.body(), DeviceModel[].class));
        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }*/
    public List<DeviceModel> getDevicesByBuilding(Long buildingId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/devices/building/" + buildingId))
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
                    .DELETE()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }








}
