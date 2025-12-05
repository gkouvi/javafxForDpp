package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.SiteModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;
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
 * REST client for interacting with /sites endpoints.
 */
public class SiteServiceClient extends ApiClient {

    public List<SiteModel> getAllSites() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/sites"))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(response.body(), SiteModel[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public SiteModel getSite(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/sites/" + id))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), SiteModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SiteModel createSite(SiteModel site) {
        try {
            String json = mapper.writeValueAsString(site);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/sites"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), SiteModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadSiteQr(Long siteId, BufferedImage qr) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qr, "png", baos);
            byte[] png = baos.toByteArray();

            String boundary = "----DPPSITEQRUPLOAD1234";
            byte[] multipart = MultipartUtil.buildMultipart(png, boundary, "file", "qr.png");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/sites/" + siteId + "/qr"))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipart))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateSite(Long siteId, SiteModel site) {
        try {
            String json = mapper.writeValueAsString(site);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/sites/" + siteId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSite(Long siteId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/sites/" + siteId))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .DELETE()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
