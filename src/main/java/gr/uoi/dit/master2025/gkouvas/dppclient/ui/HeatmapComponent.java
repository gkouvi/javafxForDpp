package gr.uoi.dit.master2025.gkouvas.dppclient.ui;

import gr.uoi.dit.master2025.gkouvas.dppclient.model.FailureHeatmapCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class HeatmapComponent extends VBox {

    private static final int CELL_SIZE = 32;
    private static final Color BACKGROUND = Color.web("#1A1A1A");

    public HeatmapComponent(List<FailureHeatmapCell> data) {

        setSpacing(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #001A33;");

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
        for (FailureHeatmapCell cell : data) {

            Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
            rect.setArcHeight(6);
            rect.setArcWidth(6);

            Color color = computeColor(cell.getCount(), max);
            rect.setFill(color);

            String tooltipText = """
                    Ώρα: %d:00
                    Ημέρα: %s
                    Alerts: %d
                    Συσκευές:
                    %s
                    """.formatted(
                    cell.getHour(),
                    days[cell.getDay()],
                    cell.getCount(),
                    (cell.getDevices() == null || cell.getDevices().isEmpty())
                            ? " - "
                            : String.join("\n - ", cell.getDevices())
            );

            Tooltip.install(rect, new Tooltip(tooltipText));

            GridPane.setConstraints(rect, cell.getHour() + 1, cell.getDay() + 1);
            grid.getChildren().add(rect);
        }

        getChildren().add(grid);
    }

    /** Heat color scaling */
    private Color computeColor(int count, int max) {
        if (count == 0) return BACKGROUND;

        double ratio = (double) count / max;  // scale 0→1

        return Color.hsb(
                0,              // κόκκινο hue
                0.8,            // saturation
                0.4 + ratio * 0.6   // brightness scaling
        );
    }
}

