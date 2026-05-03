package game;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class PowerUp {
    private double x, y, size = 20;
    private double speed = 140;
    private Type type;

    public enum Type {
        EXPAND("E"),
        SHRINK("S"),
        SPEED_UP("F"),
        SLOW_DOWN("L"),
        EXTRA_LIFE("♥"),
        FIREBALL("🔥"); // Fireball power

        public final String symbol;
        Type(String s) { this.symbol = s; }
    }

    public PowerUp(double x, double y) {
        this.x = x - size/2;
        this.y = y - size/2;
        Type[] types = Type.values();
        this.type = types[(int)(Math.random()*types.length)];
    }

    public void update(double dt) { y += speed * dt; }

    public void render() {
        GameApp.gc.setGlobalAlpha(0.95);
        GameApp.gc.setFill(Color.web("#222222", 0.08));
        GameApp.gc.fillOval(x-2, y-2, size+4, size+4);

        switch(type) {
            case EXPAND -> GameApp.gc.setFill(Color.web("#4caf50"));
            case SHRINK -> GameApp.gc.setFill(Color.web("#f44336"));
            case SPEED_UP -> GameApp.gc.setFill(Color.web("#2196f3"));
            case SLOW_DOWN -> GameApp.gc.setFill(Color.web("#ff9800"));
            case EXTRA_LIFE -> GameApp.gc.setFill(Color.web("#ffd700"));
            case FIREBALL -> GameApp.gc.setFill(Color.ORANGE);
            default -> GameApp.gc.setFill(Color.web("#888888"));
        }
        GameApp.gc.fillOval(x, y, size, size);

        GameApp.gc.setFill(Color.web("#000000DD"));
        GameApp.gc.setFont(javafx.scene.text.Font.font("Segoe UI", 12));
        Text t = new Text(type.symbol);
        t.setFont(GameApp.gc.getFont());
        double tw = t.getLayoutBounds().getWidth();
        GameApp.gc.fillText(type.symbol, x + size/2.0 - tw/2.0, y + size/2.0 + 4);

        GameApp.gc.setGlobalAlpha(1.0);
    }

    public boolean intersects(Paddle p) {
        return x + size >= p.getX() && x <= p.getX() + p.getWidth()
                && y + size >= p.getY() && y <= p.getY() + p.getHeight();
    }

    public void apply(Paddle paddle, Ball ball) {
        switch(type) {
            case EXPAND -> paddle.expand();
            case SHRINK -> paddle.shrink();
            case SPEED_UP -> ball.setSpeed(Math.min(600, Math.abs(ball.getDx()) * 1.25));
            case SLOW_DOWN -> ball.setSpeed(Math.max(120, Math.abs(ball.getDx()) * 0.92)); // mild slow
            case EXTRA_LIFE -> { if (GameApp.gameLoop != null) GameApp.gameLoop.addLife(1); }
            case FIREBALL -> {
                ball.setFire(true);
                ball.setSpeed(Math.min(600, Math.abs(ball.getDx()) * 1.3)); // slightly faster
            }
        }
    }

    public double getY() { return y; }
}
