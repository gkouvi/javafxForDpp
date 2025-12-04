package gr.uoi.dit.master2025.gkouvas.dppclient.model;

public class MaintenanceStats {

    public long total;
    public long thisMonth;
    public long thisWeek;
    public long today;
    public long overdue;

    public MaintenanceStats(long total, long thisMonth, long thisWeek, long today, long overdue) {
        this.total = total;
        thisMonth = thisMonth;
        thisWeek = thisWeek;
        this.today = today;
        this.overdue = overdue;
    }

    public MaintenanceStats() {}
}
