package gr.uoi.dit.master2025.gkouvas.dppclient.ui;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.FailureHeatmapCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeatmapComponent extends VBox {

    private static final int CELL_SIZE = 28;
    private static final Color BACKGROUND = Color.web("#0E223A");

    public HeatmapComponent(List<FailureHeatmapCell> data) {

        Map<String, FailureHeatmapCell> lookup = new HashMap<>();

        for (FailureHeatmapCell cell : data) {
            String key = cell.getDay() + "-" + cell.getHour();
            lookup.put(key, cell);
        }
        setSpacing(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #0E223A;");

        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setAlignment(Pos.CENTER_LEFT);

        // ==== HOURS (0–23) ====
        for (int h = 0; h < 24; h++) {
            Label lbl = new Label(String.valueOf(h));
            lbl.setTextFill(Color.LIGHTGRAY);
            GridPane.setConstraints(lbl, h + 1, 0);
            grid.getChildren().add(lbl);
        }

        // ==== DAYS LABELS ====
        String[] days = {"Κυρ", "Δευ", "Τρι", "Τετ", "Πεμ", "Παρ", "Σαβ"};

        for (int d = 0; d < 7; d++) {
            Label lbl = new Label(days[d]);
            lbl.setTextFill(Color.LIGHTGRAY);
            GridPane.setConstraints(lbl, 0, d + 1);
            grid.getChildren().add(lbl);
        }

        // ==== MIN/MAX for color scaling ====
        int max = data.stream().mapToInt(FailureHeatmapCell::getCount).max().orElse(1);

        // ==== DRAW CELLS ====
        for (int d = 0; d < 7; d++) {
            for (int h = 0; h < 24; h++) {

                String key = d + "-" + h;
                FailureHeatmapCell cell = lookup.get(key);

                int count = (cell == null) ? 0 : cell.getCount();

                Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
                rect.setArcHeight(6);
                rect.setArcWidth(6);

                Color color = computeColor(count, max);
                rect.setFill(color);

                if (cell != null && cell.getCount() > 0) {

                    String tooltipText = """
                Ώρα: %d:00
                Ημέρα: %s
                Alerts: %d
                Συσκευές:
                %s
                """.formatted(
                            h,
                            days[d],
                            cell.getCount(),
                            (cell.getDevices() == null || cell.getDevices().isEmpty())
                                    ? " - "
                                    : String.join("\n - ", cell.getDevices())
                    );

                    Tooltip.install(rect, new Tooltip(tooltipText));
                }

                GridPane.setConstraints(rect, h + 1, d + 1);
                grid.getChildren().add(rect);
            }
        }


        getChildren().add(grid);
    }

    /**
     * Heatmap color scale:
     * 0 failures  -> Green
     * 1+ failures -> Lime → Yellow → Orange → Red
     */
    private Color computeColor(int count, int max) {

        // 0 βλάβες → καθαρό πράσινο
        if (count == 0) {
            return Color.hsb(
                    120,   // green
                    0.75,
                    0.50
            );
        }

        // Προστασία
        if (max <= 1) {
            // αν όλα είναι 1, δείξε lime
            return Color.hsb(
                    90,    // lime
                    0.85,
                    0.90
            );
        }

        // ratio μόνο για count >= 1
        double ratio = (double) (count - 1) / (max - 1);
        ratio = Math.min(1.0, ratio);

        // Hue: Lime (90°) → Red (0°)
        double hue = 90.0 * (1.0 - ratio);

        double saturation = 0.8 + (0.2 * ratio);  // πιο έντονο όσο ανεβαίνει
        double brightness = 0.90;

        return Color.hsb(hue, saturation, brightness);
    }


}

