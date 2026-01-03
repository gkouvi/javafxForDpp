package gr.uoi.dit.master2025.gkouvas.dppclient.model;

public enum AlertStatus {
    OPEN("Ανοιχτό"),
    ACKNOWLEDGED("Αναγνωσμένο"),
    CLOSED("Κλειστό");

    private final String label;

    AlertStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

