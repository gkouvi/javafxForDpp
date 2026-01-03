package gr.uoi.dit.master2025.gkouvas.dppclient.model;

public enum DeviceStatus {
    ON("ΕΝ/ΕΝ"),
    OFF("ΕΚ/ΕΝ"),
    NOTAVAILABLE("U/S");

    private final String label;

    DeviceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

