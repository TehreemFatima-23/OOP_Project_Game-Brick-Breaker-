package game;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundManager {

    private MediaPlayer bgPlayer;
    private double musicVolume = 0.5;
    private boolean muted = false;

    private AudioClip clickSound;
    private AudioClip wallHit;
    private AudioClip paddleHit;
    private AudioClip brickHit;
    private AudioClip powerup;
    private AudioClip lifeLost;
    private AudioClip gameOver;

    private AudioClip levelCompleted;  // add this with baaki AudioClip declarations

    public void init() {
        clickSound = loadClip("/sounds/click.wav");
        wallHit = loadClip("/sounds/wall_hit.wav");
        paddleHit = loadClip("/sounds/paddle_hit.wav");
        brickHit = loadClip("/sounds/brick_hit.wav");
        powerup = loadClip("/sounds/powerup.wav");
        lifeLost = loadClip("/sounds/life_lost.wav");
        gameOver = loadClip("/sounds/game_over.wav");
        levelCompleted = loadClip("/sounds/level_completed.wav"); // add this
    }

    // then add a public method:
    public void playLevelCompleted() {
        playClip(levelCompleted);
    }


    private AudioClip loadClip(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) return new AudioClip(url.toExternalForm());
            else System.err.println("Sound not found: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // -------- Background Music --------
    public void playBackground(String path, boolean loop) {
        stopBackground();
        try {
            Media media = new Media(getClass().getResource(path).toExternalForm());
            bgPlayer = new MediaPlayer(media);
            bgPlayer.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
            bgPlayer.setVolume(muted ? 0 : musicVolume);
            bgPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBackground() {
        if (bgPlayer != null) {
            bgPlayer.stop();
            bgPlayer = null;
        }
    }

    public void pauseBackground() {
        if (bgPlayer != null) bgPlayer.pause();
    }

    public void resumeBackground() {
        if (bgPlayer != null) bgPlayer.play();
    }

    // -------- Sound Effects --------
    public void playClick() { playClip(clickSound); }
    public void playWallHit() { playClip(wallHit); }
    public void playPaddleHit() { playClip(paddleHit); }
    public void playBrickHit() { playClip(brickHit); }
    public void playPowerup() { playClip(powerup); }
    public void playLifeLost() { playClip(lifeLost); }
    public void playGameOver() { playClip(gameOver); }

    private void playClip(AudioClip clip) {
        if (clip != null && !muted) clip.play();
    }

    // -------- Volume & Mute --------
    public void setVolume(double vol) {
        musicVolume = vol;
        if (bgPlayer != null && !muted) bgPlayer.setVolume(musicVolume);
    }

    public double getMusicVolume() { return musicVolume; }

    public void setMuted(boolean m) {
        muted = m;
        if (bgPlayer != null) bgPlayer.setVolume(muted ? 0 : musicVolume);
    }

    public boolean isMuted() { return muted; }
}