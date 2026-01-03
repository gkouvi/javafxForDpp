package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import java.time.LocalDate;

public class MaintenanceDailySummaryModel {
//για το BarChart των συντηρήσεων
    private LocalDate date;

    private long completed;
    private long canceleld;
    private long overdue;

    public LocalDate getDate() {
        return date;
    }
    public long getCanceleld(){
        return canceleld;
    }



    public long getCompleted() {
        return completed;
    }


    public long getOverdue() {return overdue;}


}
