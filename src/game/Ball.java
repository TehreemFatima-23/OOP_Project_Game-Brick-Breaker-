package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball {
    private double x, y;
    private double radius = 10;
    private double dx = 180, dy = -180; // pixels/sec
    private boolean attached = true;
    private Paddle attachedPaddle;
    private boolean fire = false;

    public Ball() {
        this.x = 400; // center by default
        this.y = 300;
    }

    public void attachTo(Paddle p) {
        attached = true;
        attachedPaddle = p;
        fire = false; // reset fire mode when attached
        resetPosition();
    }

    public boolean isAttached() { return attached; }

    public void launch() {
        if (!attached) return;
        attached = false;
        dx = 180;
        dy = -180;
    }

    public void followPaddle(Paddle p) {
        if (!attached) return;
        this.x = p.getX() + p.getWidth() / 2.0;
        this.y = p.getY() - radius - 2;
    }

    public void resetPosition() {
        if (attachedPaddle != null) followPaddle(attachedPaddle);
    }

    public void update(double dt, SoundManager sm) {
        if (attached) return;

        x += dx * dt;
        y += dy * dt;

        // Wall collisions
        if (x - radius < 0) {
            x = radius;
            dx *= -1;
            if (sm != null) sm.playWallHit();
        } else if (x + radius > Constants.WIDTH) {
            x = Constants.WIDTH - radius;
            dx *= -1;
            if (sm != null) sm.playWallHit();
        }

        if (y - radius < 0) {
            y = radius;
            dy *= -1;
            if (sm != null) sm.playWallHit();
        }
        // bottom check handled in GameLoop
    }

    public boolean intersects(Paddle p) {
        return x + radius > p.getX() && x - radius < p.getX() + p.getWidth()
                && y + radius > p.getY() && y - radius < p.getY() + p.getHeight();
    }

    public boolean intersects(Brick b) {
        return x + radius > b.getX() && x - radius < b.getX() + b.getWidth()
                && y + radius > b.getY() && y - radius < b.getY() + b.getHeight();
    }

    public void bounceOffPaddle(Paddle p) {
        double paddleCenter = p.getX() + p.getWidth()/2.0;
        double distanceFromCenter = x - paddleCenter;
        double percent = distanceFromCenter / (p.getWidth()/2.0);
        dx = percent * 300;
        dy = -Math.abs(dy);
    }

    public void bounceOffBrick(Brick b) {
        dy *= -1;
    }

    public void hitBrick(Brick b) {
        b.hit();
        if (fire) {
            b.hit(); // extra damage for fireball
        }
        bounceOffBrick(b);
    }

    public void render() {
        GraphicsContext gc = GameApp.gc;
        if(fire) gc.setFill(Color.ORANGE); // fireball color
        else gc.setFill(Color.web("#ffffff"));
        gc.fillOval(x - radius, y - radius, radius*2, radius*2);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }

    public void setSpeed(double s) {
        double angle = Math.atan2(dy, dx);
        dx = s * Math.cos(angle);
        dy = s * Math.sin(angle);
    }

    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }

    public void setFire(boolean f) { fire = f; }
    public boolean isFire() { return fire; }
}
