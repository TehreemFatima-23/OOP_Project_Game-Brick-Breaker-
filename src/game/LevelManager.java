package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.List;

public class LevelManager {

    /**
     * Populate the provided bricks list according to the chosen level.
     * Uses the project's Brick(double x, double y, int hp) constructor.
     */
    public static void createLevel(List<Brick> out, int level, Ball ball) {
        out.clear();

        double gap = 6;
        double startX = 60;
        double startY = 60;
        int cols = 10;
        int rows;

        switch (level) {
            case 1:
                rows = 5 + Math.min(3, level - 1); // same style as original
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        double x = startX + c * (60 + gap);
                        double y = startY + r * (24 + gap);
                        int hp = 1 + r / 2; // 1,1,2,2,3...
                        out.add(new Brick(x, y, hp));
                    }
                }
                if (ball != null) ball.setSpeed(200);
                break;

            case 2:
                rows = 5 + Math.min(3, level - 1);
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        if ((r + c) % 2 == 0) {
                            double x = startX + c * (60 + gap);
                            double y = startY + r * (24 + gap);
                            int hp = 1 + r / 2;
                            out.add(new Brick(x, y, hp));
                        }
                    }
                }
                if (ball != null) ball.setSpeed(250);
                break;

            case 3:
                rows = 5 + Math.min(3, level - 1);
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        if (r % 2 == 0 || c % 3 == 0) {
                            double x = startX + c * (60 + gap);
                            double y = startY + r * (24 + gap);
                            int hp = 1 + r / 2;
                            out.add(new Brick(x, y, hp));
                        }
                    }
                }
                if (ball != null) ball.setSpeed(300);
                break;

            case 4:
                rows = 6; // slightly different pattern
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        if ((r + c) % 3 != 0) {
                            double x = startX + c * (60 + gap);
                            double y = startY + r * (24 + gap);
                            int hp = 1 + (r/2);
                            out.add(new Brick(x, y, hp));
                        }
                    }
                }
                if (ball != null) ball.setSpeed(350);
                break;

            case 5:
                rows = 6;
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        if ((r + c) % 2 == 0 || (c % 4 == 0)) {
                            double x = startX + c * (60 + gap);
                            double y = startY + r * (24 + gap);
                            int hp = 1 + (r/2);
                            out.add(new Brick(x, y, hp));
                        }
                    }
                }
                if (ball != null) ball.setSpeed(400);
                break;

            default:
                // fallback: simple single row
                for (int c = 0; c < cols; c++) {
                    double x = startX + c * (60 + gap);
                    double y = startY;
                    out.add(new Brick(x, y, 1));
                }
                if (ball != null) ball.setSpeed(200);
                break;
        }
    }

    /**
     * Draw a small preview of level layout onto a GraphicsContext (used in level selection).
     * This uses simple rectangles and colors — visually matches gameplay patterns.
     */
    public static void drawLevelPreview(GraphicsContext pgc, int level) {
        // nice background for preview
        Stop[] stops = new Stop[] { new Stop(0, Color.web("#0f0c29")), new Stop(1, Color.web("#302b63")) };
        pgc.setFill(new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE, stops));
        pgc.fillRect(0,0, pgc.getCanvas().getWidth(), pgc.getCanvas().getHeight());

        double gap = 4;
        double startX = 8;
        double startY = 8;
        int cols = 8;
        int rows = 3 + Math.min(2, level - 1);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean place;
                switch (level) {
                    case 1 -> place = true;
                    case 2 -> place = ((r + c) % 2 == 0);
                    case 3 -> place = (r % 2 == 0 || c % 3 == 0);
                    case 4 -> place = ((r + c) % 3 != 0);
                    case 5 -> place = ((r + c) % 2 == 0 || (c % 4 == 0));
                    default -> place = true;
                }
                if (!place) continue;

                double w = (pgc.getCanvas().getWidth() - 2*startX - (cols-1)*gap)/cols;
                double h = 10;
                double x = startX + c * (w + gap);
                double y = startY + r * (h + gap);

                Color fill = r % 3 == 0 ? Color.web("#66bb6a") : (r % 3 == 1 ? Color.web("#ff8f00") : Color.web("#b71c1c"));
                pgc.setFill(fill.deriveColor(0,1,1,0.95));
                pgc.fillRoundRect(x, y, w, h, 4, 4);
                pgc.setStroke(Color.web("#00000040"));
                pgc.strokeRoundRect(x, y, w, h, 4, 4);
            }
        }
    }
}