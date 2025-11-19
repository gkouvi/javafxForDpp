package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import java.time.LocalDateTime;

/**
 * UI Model for representing a device-related document.
 */
public class DocumentModel {

    private Long id;
    private Long deviceId;
    private String filename;
    private String fileType;
    private LocalDateTime uploadedAt;

    /** Binary file content (used only for downloads/uploads) */
    private byte[] data;

    public DocumentModel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
