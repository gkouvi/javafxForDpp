package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.DocumentModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.session.UserSession;

import java.net.URI;
import java.io.File;
import java.nio.file.Files;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

/**
 * REST client for interacting with /documents endpoints.
 */







public class DocumentServiceClient extends ApiClient {

    // ===========================================================
    // GET ALL DOCUMENTS FOR DEVICE
    // ===========================================================
    public List<DocumentModel> getDocumentsByDevice(Long deviceId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/documents/device/" + deviceId))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Arrays.asList(mapper.readValue(response.body(), DocumentModel[].class));

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ===========================================================
    // DOWNLOAD DOCUMENT AS BYTE[]
    // ===========================================================
    public byte[] downloadDocument(Long docId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/documents/download/" + docId))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .GET()
                    .build();

            HttpResponse<byte[]> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===========================================================
    // UPLOAD DOCUMENT (MULTIPART)
    // ===========================================================
    public boolean uploadDocument(Long deviceId, File file) {
        try {
            String boundary = "----DPPCLIENTBOUNDARY" + System.currentTimeMillis();

            var byteContent = Files.readAllBytes(file.toPath());

            String partHeader =
                    "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
                            "Content-Type: application/octet-stream\r\n\r\n";

            String partFooter = "\r\n--" + boundary + "--\r\n";

            byte[] headerBytes = partHeader.getBytes();
            byte[] footerBytes = partFooter.getBytes();

            byte[] multipartData = new byte[
                    headerBytes.length + byteContent.length + footerBytes.length
                    ];

            System.arraycopy(headerBytes, 0, multipartData, 0, headerBytes.length);
            System.arraycopy(byteContent, 0, multipartData, headerBytes.length, byteContent.length);
            System.arraycopy(footerBytes, 0, multipartData,
                    headerBytes.length + byteContent.length,
                    footerBytes.length);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/documents/upload/" + deviceId))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipartData))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );


            return response.statusCode() >= 200 && response.statusCode() < 300;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===========================================================
    // DELETE DOCUMENT
    // ===========================================================
    public boolean deleteDocument(Long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/documents/delete/" + id))
                    .header("Authorization", "Bearer " + UserSession.getToken())
                    .DELETE()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() >= 200 && response.statusCode() < 300;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


