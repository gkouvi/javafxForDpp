package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.BuildingModel;
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
 * REST client for interacting with /buildings endpoints.
 */
public class BuildingServiceClient extends ApiClient {

    public List<BuildingModel> getAllBuildings() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/buildings"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(response.body(), BuildingModel[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public BuildingModel getBuilding(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/buildings/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), BuildingModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the list of buildings belonging to a specific site.
     * This requires that backend supports /buildings?siteId= (if not, θα σου το φτιάξω).
     */
    public List<BuildingModel> getBuildingsBySite(Long siteId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/buildings/site/" + siteId))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(
                    mapper.readValue(response.body(), BuildingModel[].class)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public BuildingModel createBuilding(BuildingModel building) {
        try {
            String json = mapper.writeValueAsString(building);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/buildings"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), BuildingModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadBuildingQr(Long buildingId, BufferedImage qr) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qr, "png", baos);
            byte[] png = baos.toByteArray();

            String boundary = "----DPPBUILDINGQR1234";
            byte[] multipart = MultipartUtil.buildMultipart(png, boundary, "file", "qr.png");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/buildings/" + buildingId + "/qr"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipart))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBuilding(Long buildingId, BuildingModel building) {
        try {
            String json = mapper.writeValueAsString(building);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/buildings/" + buildingId))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteBuilding(Long buildingId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/buildings/" + buildingId))
                    .DELETE()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

