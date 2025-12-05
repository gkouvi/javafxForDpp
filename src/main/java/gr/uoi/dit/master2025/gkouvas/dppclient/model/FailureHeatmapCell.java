package gr.uoi.dit.master2025.gkouvas.dppclient.model;


import java.util.List;

public class FailureHeatmapCell {
    public int hour;
    public int day;
    public int count;
    public List<String> devices;

    public FailureHeatmapCell() {}

    public int getHour() { return hour; }
    public int getDay() { return day; }
    public int getCount() { return count; }
    public List<String> getDevices() { return devices; }
}
