package game;

import javafx.scene.paint.Color;

public class Particle {
    double x,y,vx,vy,life;

    public Particle(double sx, double sy) {
        x = sx; y = sy;
        double a = Math.random()*Math.PI*2;
        double s = 60 + Math.random()*240;
        vx = Math.cos(a)*s;
        vy = Math.sin(a)*s;
        life = 0.8 + Math.random()*1.2;
    }

    public void update(double dt) {
        x += vx * dt;
        y += vy * dt;
        vy += 400 * dt; // gravity
        life -= dt;
    }

    public void render() {
        if (life <= 0) return;
        double alpha = Math.max(0, life/1.6);
        GameApp.gc.setFill(Color.web("#ffd54f", alpha));
        GameApp.gc.fillOval(x, y, 6, 6);
    }
}
