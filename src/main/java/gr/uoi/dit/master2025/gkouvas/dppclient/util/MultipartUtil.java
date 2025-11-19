package gr.uoi.dit.master2025.gkouvas.dppclient.util;

import java.nio.charset.StandardCharsets;

public class MultipartUtil {

    public static byte[] buildMultipart(byte[] fileBytes, String boundary, String fieldName, String filename) {
        String part1 =
                "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + filename + "\"\r\n" +
                        "Content-Type: image/png\r\n\r\n";

        String part2 = "\r\n--" + boundary + "--\r\n";

        byte[] header = part1.getBytes(StandardCharsets.UTF_8);
        byte[] footer = part2.getBytes(StandardCharsets.UTF_8);

        byte[] full = new byte[header.length + fileBytes.length + footer.length];

        System.arraycopy(header, 0, full, 0, header.length);
        System.arraycopy(fileBytes, 0, full, header.length, fileBytes.length);
        System.arraycopy(footer, 0, full, header.length + fileBytes.length, footer.length);

        return full;
    }
}
