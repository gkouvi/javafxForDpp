package gr.uoi.dit.master2025.gkouvas.dppclient.model;

public class MaintenanceBoxesKpiModel {
    private long total;
    private long completed;
    private long pending;
    private long overdue;
    private long cancel;
    private String escalated;
    private String autoClosed;

    public String getEscalated() {
        return escalated;
    }

    public String getAutoClosed() {
        return autoClosed;
    }

    public long getCancel() {
        return cancel;
    }

    public long getTotal() {
        return total;
    }

    public long getCompleted() {
        return completed;
    }

    public long getPending() {
        return pending;
    }

    public long getOverdue() {
        return overdue;
    }
}
