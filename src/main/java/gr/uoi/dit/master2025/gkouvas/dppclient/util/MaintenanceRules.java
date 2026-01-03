package gr.uoi.dit.master2025.gkouvas.dppclient.util;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceModel;
import gr.uoi.dit.master2025.gkouvas.dppclient.model.MaintenanceStatus;

import java.time.LocalDate;

public final class MaintenanceRules {

    private MaintenanceRules() {}

    public static boolean isPlanned(MaintenanceModel m, LocalDate today) {
        if (m == null || today == null) return false;

        return m.getStatus() == MaintenanceStatus.PENDING
                && m.getPlannedDate() != null
                && m.getPlannedDate().isAfter(today);
    }




    public static boolean isCompleted(MaintenanceModel m) {
        return m != null
                && m.getStatus() == MaintenanceStatus.COMPLETED
                && m.getPerformedDate() != null;
    }




    public static boolean isOverdue(MaintenanceModel m, LocalDate today) {
        if (m == null || today == null) return false;

        return m.getStatus() == MaintenanceStatus.PENDING
                && m.getPlannedDate() != null
                && m.getPlannedDate().isBefore(today);
    }




    public static boolean isPending(MaintenanceModel m, LocalDate today) {
        if (m == null || today == null) return false;

        return m.getStatus() == MaintenanceStatus.PENDING
                && m.getPlannedDate() != null
                && !m.getPlannedDate().isAfter(today); // ≤ today
    }




    public static boolean matches(
            MaintenanceModel m,
            LocalDate date,
            MaintenanceCategory category,
            LocalDate today
    ) {
        if (m == null || date == null || category == null)
            return false;

        // ⛔ ποτέ cancelled
        if (m.getStatus() == MaintenanceStatus.CANCELLED)
            return false;

        return switch (category) {

            // ================= PLANNED =================
            case PLANNED ->
                    m.getPlannedDate() != null
                            && m.getPlannedDate().equals(date);

            // ================= COMPLETED =================
            case COMPLETED ->
                    m.getStatus() == MaintenanceStatus.COMPLETED
                            && m.getPerformedDate() != null
                            && m.getPerformedDate().equals(date);

            // ================= PENDING (ΣΗΜΕΡΑ / ΜΕΛΛΟΝ) =================
            case PENDING ->
                    m.getStatus() == MaintenanceStatus.PENDING
                            && m.getPlannedDate() != null
                            && m.getPlannedDate().equals(date)
                            && !m.getPlannedDate().isBefore(today);

            // ================= OVERDUE =================
            case OVERDUE ->
                    m.getStatus() == MaintenanceStatus.PENDING
                            && m.getPlannedDate() != null
                            && m.getPlannedDate().equals(date)
                            && m.getPlannedDate().isBefore(today);
        };
    }




    public static boolean isCancelled(MaintenanceModel m) {
        return m != null && m.getStatus() == MaintenanceStatus.CANCELLED;
    }


}

