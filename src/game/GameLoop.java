package game;

import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.*;

public class GameLoop extends AnimationTimer {
    private long lastTime = 0;
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;
    private List<PowerUp> powerUps;
    private int lives, score, topScore;
    private boolean paused = false;
    private Random rnd = new Random();
    private GameApp app;
    private int currentLevel = 1;
    private int startingLevel = 1;
    private final int MAX_LEVELS = 5;

    public GameLoop(GameApp app) {
        this.app = app;
        bricks = new ArrayList<>();
        powerUps = new ArrayList<>();
        paddle = new Paddle();
        ball = new Ball();
        lives = 3;
        score = 0;
        loadTopScore();
        LevelManager.createLevel(bricks, currentLevel, ball);
        ball.attachTo(paddle);
    }

    public void setStartingLevel(int lvl) {
        if (lvl >= 1 && lvl <= MAX_LEVELS) this.startingLevel = lvl;
        this.currentLevel = startingLevel;
        LevelManager.createLevel(bricks, currentLevel, ball);
    }

    public void setPaused(boolean p) { this.paused = p; }
    public boolean isPaused() { return paused; }

    private void loadTopScore() {
        topScore = 0;
        try {
            List<HighScoreManager.Entry> top = HighScoreManager.loadTop(1);
            if (!top.isEmpty()) topScore = top.get(0).score;
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void addLife(int n) { lives += n; }

    public void resetAll() {
        bricks.clear();
        currentLevel = startingLevel;
        LevelManager.createLevel(bricks, currentLevel, ball);
        powerUps.clear();
        paddle = new Paddle();
        ball = new Ball();
        lives = 3;
        score = 0;
        paused = false;
        lastTime = 0;
        loadTopScore();
        ball.attachTo(paddle);
        renderBackgroundOnly();
        paddle.render();
        ball.render();

        if (app.getSoundManager() != null)
            app.getSoundManager().playBackground("/sounds/bg_loop.mp3", true);
    }

    @Override
    public void handle(long now) {
        if (lastTime == 0) { lastTime = now; return; }
        double dt = (now - lastTime) / 1e9;
        lastTime = now;

        // Toggle pause with SPACE
        if (InputHandler.space) {
            paused = !paused;
            InputHandler.space = false;
            if (paused) {
                if (app.getSoundManager() != null) app.getSoundManager().pauseBackground();
                app.showInGameMenu();
            } else {
                if (app.getSoundManager() != null) app.getSoundManager().resumeBackground();
                app.getOverlayBox().setVisible(false);
                if (app.canvas != null && app.canvas.getScene() != null && app.canvas.getScene().getRoot() != null)
                    app.canvas.getScene().getRoot().requestFocus();
            }
        }

        // Launch ball with UP/W
        if (ball.isAttached() && InputHandler.up && !paused) {
            ball.launch();
            InputHandler.up = false;
        }

        if (!paused) update(dt);
        render();
    }

    private void update(double dt) {
        paddle.update(dt);

        if (ball.isAttached()) {
            ball.followPaddle(paddle);
        } else {
            ball.update(dt, app.getSoundManager());
        }

        // Ball hits paddle
        if (!ball.isAttached() && ball.intersects(paddle) && ball.getDy() > 0) {
            ball.bounceOffPaddle(paddle);
            if (app.getSoundManager() != null) app.getSoundManager().playPaddleHit();
        }

        // Ball hits bricks
        Iterator<Brick> it = bricks.iterator();
        while (it.hasNext()) {
            Brick b = it.next();
            if (!b.isDestroyed() && ball.intersects(b)) {
                b.hit();
                ball.bounceOffBrick(b);
                score += b.getPoints();
                if (score > topScore) topScore = score;
                if (app.getSoundManager() != null) app.getSoundManager().playBrickHit();

                if (rnd.nextDouble() < 0.18)
                    powerUps.add(new PowerUp(b.getX() + b.getWidth()/2, b.getY() + b.getHeight()/2));

                if (b.isDestroyed()) it.remove();
            }
        }

        // Powerups falling
        Iterator<PowerUp> pit = powerUps.iterator();
        while (pit.hasNext()) {
            PowerUp p = pit.next();
            p.update(dt);
            if (p.intersects(paddle)) {
                p.apply(paddle, ball);
                if (app.getSoundManager() != null) app.getSoundManager().playPowerup();
                pit.remove();
            } else if (p.getY() > Constants.HEIGHT) pit.remove();
        }

        // Ball falls below screen
        if (ball.getY() > Constants.HEIGHT) {
            lives--;
            if (app.getSoundManager() != null) app.getSoundManager().playLifeLost();
            if (lives <= 0) {
                stop();
                if (app.getSoundManager() != null) app.getSoundManager().playGameOver();
                app.showGameOver(score);
                loadTopScore();
                return;
            } else {
                ball.attachTo(paddle);
                paddle.reset();
            }
        }

        // Level cleared
        if (bricks.isEmpty()) {
            if (app.getSoundManager() != null) app.getSoundManager().playLevelCompleted();

            int unlocked = ProgressManager.loadHighestUnlocked();
            if (currentLevel >= unlocked && currentLevel < MAX_LEVELS)
                ProgressManager.saveHighestUnlocked(currentLevel + 1);

            currentLevel++;
            if (currentLevel > MAX_LEVELS) {
                stop();
                app.showLevelComplete(score);
                return;
            } else {
                LevelManager.createLevel(bricks, currentLevel, ball);
                ball.attachTo(paddle);
                paddle.reset();
            }
        }
    }

    private void render() {
        app.drawBackground();

        // Score
        GameApp.gc.setFill(Color.web("#ffffffDD"));
        GameApp.gc.setFont(javafx.scene.text.Font.font("Segoe UI", 16));
        GameApp.gc.fillText("Score: " + score, 12, 24);

        // Top Score
        String top = "Top: " + topScore;
        Text topText = new Text(top);
        topText.setFont(GameApp.gc.getFont());
        double topWidth = topText.getLayoutBounds().getWidth();
        GameApp.gc.fillText(top, Constants.WIDTH / 2.0 - topWidth / 2.0, 28);

        // Level display
        GameApp.gc.setFont(javafx.scene.text.Font.font("Segoe UI", 13));
        String lvl = "Level " + currentLevel;
        Text lvlText = new Text(lvl);
        lvlText.setFont(GameApp.gc.getFont());
        double lvlW = lvlText.getLayoutBounds().getWidth();
        GameApp.gc.fillText(lvl, Constants.WIDTH / 2.0 - lvlW / 2.0, 42);

        // Lives as hearts
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < Math.max(0, lives); i++) hearts.append("♥ ");
        GameApp.gc.setFont(javafx.scene.text.Font.font("Segoe UI", 18));
        GameApp.gc.setFill(Color.web("#ff6b6b"));
        GameApp.gc.fillText(hearts.toString().trim(), Constants.WIDTH - 140, 24);

        GameApp.gc.setFill(Color.web("#ffffffDD"));
        GameApp.gc.setFont(javafx.scene.text.Font.font("Segoe UI", 12));
        GameApp.gc.fillText("Lives", Constants.WIDTH - 70, 24);

        // Entities render
        paddle.render();
        ball.render();
        for (Brick b : bricks) b.render();
        for (PowerUp p : powerUps) p.render();
    }

    public void renderBackgroundOnly() { app.drawBackground(); }
}