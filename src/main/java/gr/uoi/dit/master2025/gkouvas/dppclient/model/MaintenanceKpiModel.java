package gr.uoi.dit.master2025.gkouvas.dppclient.model;

public class MaintenanceKpiModel {
    public long critical;
    public long urgent;
    public long overdue;
    public long thisMonth;

    // getters + setters

    public long getCritical() {
        return critical;
    }

    public void setCritical(long critical) {
        this.critical = critical;
    }

    public long getUrgent() {
        return urgent;
    }

    public void setUrgent(long urgent) {
        this.urgent = urgent;
    }

    public long getOverdue() {
        return overdue;
    }

    public void setOverdue(long overdue) {
        this.overdue = overdue;
    }

    public long getThisMonth() {
        return thisMonth;
    }

    public void setThisMonth(long thisMonth) {
        this.thisMonth = thisMonth;
    }
}

