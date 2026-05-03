package game;

import javafx.scene.paint.Color;

public class Paddle {
    private double x = Constants.WIDTH / 2 - 60;
    private double y = Constants.HEIGHT - 60;
    private double width = 120;
    private double height = 16;
    private double speed = 420;

    public void update(double dt) {
        if (InputHandler.left) x -= speed * dt;
        if (InputHandler.right) x += speed * dt;
        if (x < 0) x = 0;
        if (x + width > Constants.WIDTH) x = Constants.WIDTH - width;
    }

    public void render() {
        GameApp.gc.setFill(Color.web("#4fd1c5"));
        GameApp.gc.fillRoundRect(x, y, width, height, 12, 12);
        GameApp.gc.setFill(Color.web("#0b3b36", 0.25));
        GameApp.gc.fillRoundRect(x, y + height / 2, width, height / 2, 10, 10);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void expand() { width = Math.min(300, width + 40); }
    public void shrink() { width = Math.max(60, width - 40); }
    public void reset() { x = Constants.WIDTH / 2 - width / 2; }
}


