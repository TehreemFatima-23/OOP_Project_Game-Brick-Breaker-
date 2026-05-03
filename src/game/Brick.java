package game;

import javafx.scene.paint.Color;

public class Brick {
    private double x, y, width = 60, height = 24;
    private int hp;
    private int points;

    public Brick(double x, double y, int hp) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.points = 50 * hp;
    }

    public void hit() { hp--; }
    public boolean isDestroyed() { return hp <= 0; }

    public void render() {
        if (isDestroyed()) return;

        Color fill;
        if (hp >= 3) fill = Color.web("#b71c1c");
        else if (hp == 2) fill = Color.web("#ff8f00");
        else fill = Color.web("#66bb6a");

        GameApp.gc.setFill(fill);
        GameApp.gc.fillRoundRect(x, y, width, height, 8, 8);

        GameApp.gc.setStroke(Color.web("#00000055"));
        GameApp.gc.strokeRoundRect(x, y, width, height, 8, 8);

        GameApp.gc.setFill(Color.web("#000000DD"));
        GameApp.gc.fillText(String.valueOf(hp), x + width / 2 - 4, y + height / 2 + 5);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getPoints() { return points; }
}


