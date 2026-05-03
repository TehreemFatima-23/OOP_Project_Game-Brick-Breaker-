package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

public class RadialGradientPaint {
    public static void fillBall(GraphicsContext gc, double cx, double cy, double radius) {
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#FFFFFF")),
                new Stop(0.6, Color.web("#DDEEFF")),
                new Stop(1, Color.web("#4a90e2"))
        };
        RadialGradient rg = new RadialGradient(0, 0, cx, cy, radius, false, CycleMethod.NO_CYCLE, stops);
        gc.setFill(rg);
        gc.fillOval(cx - radius, cy - radius, radius*2, radius*2);

        // small highlight
        gc.setFill(Color.web("#FFFFFF", 0.8));
        gc.fillOval(cx - radius/2.2, cy - radius/1.9, radius*0.6, radius*0.6);
    }
}