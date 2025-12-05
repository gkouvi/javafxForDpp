package gr.uoi.dit.master2025.gkouvas.dppclient.model;

public class MonthCountModel {
    private String month;
    private long count;

    public String getMonth() { return month; }
    public long getCount() { return count; }

    @Override
    public String toString() {
        return month + " → " + count;
    }
}
