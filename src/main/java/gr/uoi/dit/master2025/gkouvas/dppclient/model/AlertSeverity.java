package gr.uoi.dit.master2025.gkouvas.dppclient.model;

public enum AlertSeverity {
    LOW("Χαμηλή"),
    MEDIUM("Μεσαία"),
    HIGH("Υψηλή"),
    CRITICAL("Κρίσιμη");

    private final String label;

    AlertSeverity(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

