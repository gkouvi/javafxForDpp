package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import java.time.LocalDate;

/**
 * UI Model for representing an alert associated with a device.
 */
public class AlertModel {

    private Long alertId;
    private Long deviceId;
    private String message;
    private LocalDate dueDate;
    private String status;

    public AlertModel() {}

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
