package game;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {
    public static boolean left = false;
    public static boolean right = false;
    public static boolean space = false;
    public static boolean up = false; // new: launch (UP arrow or W)

    public static void attach(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.LEFT || code == KeyCode.A) left = true;
            if (code == KeyCode.RIGHT || code == KeyCode.D) right = true;
            if (code == KeyCode.SPACE) space = true;
            if (code == KeyCode.UP || code == KeyCode.W) up = true;
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.LEFT || code == KeyCode.A) left = false;
            if (code == KeyCode.RIGHT || code == KeyCode.D) right = false;
            if (code == KeyCode.SPACE) space = false;
            if (code == KeyCode.UP || code == KeyCode.W) up = false;
        });
    }
}


