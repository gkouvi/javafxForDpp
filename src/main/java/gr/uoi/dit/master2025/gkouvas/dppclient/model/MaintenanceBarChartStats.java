package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import java.util.List;

public class MaintenanceBarChartStats {

    public List<MonthCountModel> planned;
    public List<MonthCountModel> completed;

    @Override
    public String toString() {
        return "MaintenanceBarChartStats{planned=" + planned + ", completed=" + completed + "}";
    }
}
