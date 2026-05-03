package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.List;

public class GameApp extends Application {

    public static Canvas canvas;
    public static GraphicsContext gc;
    public static GameLoop gameLoop;

    private StackPane root;
    private VBox overlayBox;
    private Label titleLabel;
    private Label infoLabel;
    private Button startBtn, exitBtn, restartBtn;
    private Button floatingMenuBtn;

    private SoundManager soundManager;

    public Button getFloatingMenuBtn() { return floatingMenuBtn; }
    public VBox getOverlayBox() { return overlayBox; }
    public SoundManager getSoundManager() { return soundManager; }

    @Override
    public void start(Stage stage) {
        canvas = new Canvas(Constants.WIDTH, Constants.HEIGHT);
        gc = canvas.getGraphicsContext2D();

        root = new StackPane();
        root.setPrefSize(Constants.WIDTH, Constants.HEIGHT);

        Pane canvasHolder = new Pane(canvas);
        canvasHolder.setStyle("-fx-background-color: transparent;");

        overlayBox = new VBox(12);
        overlayBox.setAlignment(Pos.CENTER);
        overlayBox.setPickOnBounds(true);
        overlayBox.setMouseTransparent(false);

        titleLabel = new Label("BRICK BREAKER");
        titleLabel.setFont(Font.font("Montserrat Black", 40));
        titleLabel.setTextFill(Color.web("#FFFFFFEE"));
        titleLabel.setEffect(new DropShadow(12, Color.color(0,0,0,0.6)));

        infoLabel = new Label();
        infoLabel.setFont(Font.font("Segoe UI", 16));
        infoLabel.setTextFill(Color.web("#FFFFFFDD"));

        startBtn = makeStyledButton("Start", 180, "-fx-background-color: linear-gradient(#6EE, #29A);");
        startBtn.setOnAction(e -> showLevelSelection());

        restartBtn = makeStyledButton("Restart", 140, "-fx-background-color: linear-gradient(#FFB86B, #FF7A45);");
        restartBtn.setOnAction(e -> restartGame());

        exitBtn = makeStyledButton("Exit", 140, "-fx-background-color: linear-gradient(#AAA, #777);");
        exitBtn.setOnAction(e -> {
            if (root.getScene() != null && root.getScene().getWindow() != null) {
                ((Stage) root.getScene().getWindow()).close();
            }
        });

        floatingMenuBtn = new Button("≡");
        floatingMenuBtn.setStyle("-fx-font-size:16; -fx-background-radius:8; -fx-background-color: rgba(0,0,0,0.45); -fx-text-fill: white; -fx-padding:6;");
        floatingMenuBtn.setVisible(false);
        floatingMenuBtn.setOnAction(e -> showInGameMenu());
        StackPane.setAlignment(floatingMenuBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(floatingMenuBtn, new Insets(12,12,0,0));

        soundManager = new SoundManager();
        soundManager.init();

        ToggleButton muteBtn = new ToggleButton("🔈");
        muteBtn.setStyle("-fx-background-color: rgba(20,20,20,0.55); -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 6;");
        muteBtn.setOnAction(e -> {
            boolean muted = muteBtn.isSelected();
            soundManager.setMuted(muted);
            muteBtn.setText(muted ? "🔇" : "🔈");
        });

        Slider volumeSlider = new Slider(0, 1, soundManager.getMusicVolume());
        volumeSlider.setPrefWidth(120);
        volumeSlider.valueProperty().addListener((obs, oldV, newV) -> {
            if (soundManager != null) soundManager.setVolume(newV.doubleValue());
        });

        HBox soundControls = new HBox(10, muteBtn, volumeSlider);
        soundControls.setAlignment(Pos.TOP_LEFT);
        HBox.setMargin(muteBtn, new Insets(10,0,0,10));
        HBox.setMargin(volumeSlider, new Insets(10,0,0,0));
        soundControls.setMaxWidth(Region.USE_PREF_SIZE);
        soundControls.setMouseTransparent(false);

        root.getChildren().clear();
        root.getChildren().addAll(canvasHolder, soundControls, floatingMenuBtn, overlayBox);

        Scene scene = new Scene(root);
        InputHandler.attach(scene);
        scene.setOnMouseClicked((MouseEvent e) -> root.requestFocus());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Brick Breaker \uD83D\uDD79\uFE0F\uD83E\uDDF1\uD83D\uDCA5");
        stage.show();

        root.requestFocus();
        gameLoop = new GameLoop(this);

        showStartScreen();
        renderInitialBackground();
    }

    // ------------------- UPDATED makeStyledButton -------------------
    private Button makeStyledButton(String text, double width, String extraStyle) {
        Button b = new Button(text);
        b.setPrefWidth(width);
        b.setFont(Font.font("Segoe UI Semibold", 14));
        String base = "-fx-background-radius:10; -fx-text-fill: white; -fx-padding: 8 12 8 12; -fx-font-weight: bold;";
        b.setStyle(base + extraStyle);

        b.setOnMouseEntered(e -> b.setOpacity(0.92));
        b.setOnMouseExited(e -> b.setOpacity(1.0));

        // CLICK SOUND
        b.setOnAction(e -> {
            if (soundManager != null) soundManager.playClick();
        });

        return b;
    }


    // ---------------- START SCREEN ----------------
    public void showStartScreen() {
        overlayBox.getChildren().clear();
        overlayBox.setVisible(true);
        floatingMenuBtn.setVisible(false);

        titleLabel.setText("BRICK BREAKER");
        titleLabel.setFont(Font.font("Montserrat Black", 40));
        titleLabel.setTextFill(Color.web("#FFFFFFEE"));
        titleLabel.setEffect(new DropShadow(10, Color.color(0,0,0,0.6)));

        infoLabel.setText("Use ⬅ ➡ or A / D to move. \n     Press SPACE to pause.");
        infoLabel.setFont(Font.font("Segoe UI", 14));
        infoLabel.setTextFill(Color.web("#DDDDDD"));

        Button start = makeStyledButton("Start", 220, "-fx-background-color: linear-gradient(#3AA, #157); -fx-font-size:16;");
        start.setOnAction(e -> showLevelSelection());

        Button high = makeStyledButton("High Scores", 220, "-fx-background-color: linear-gradient(#6B4F1A, #3E2A0A); -fx-font-size:16;");
        high.setOnAction(e -> HighScoreScreen.showHighScores(this));

        Button exit = makeStyledButton("Exit", 220, "-fx-background-color: linear-gradient(#555, #333); -fx-font-size:16;");
        exit.setOnAction(e -> {
            if (root.getScene() != null && root.getScene().getWindow() != null) {
                ((Stage) root.getScene().getWindow()).close();
            }
        });

        VBox buttons = new VBox(12, start, high, exit);
        buttons.setAlignment(Pos.CENTER);

        VBox card = new VBox(14, titleLabel, infoLabel, buttons);
        card.setAlignment(Pos.CENTER);
        card.setMinWidth(420);
        card.setStyle("-fx-background-color: linear-gradient(rgba(10,10,10,0.85), rgba(20,20,20,0.85));"
                + " -fx-background-radius: 14; -fx-padding: 22; -fx-border-color: rgba(255,255,255,0.03); -fx-border-radius:14;");

        overlayBox.getChildren().add(card);
        root.requestFocus();
    }

    // ---------------- LEVEL SELECTION ----------------
    public void showLevelSelection() {
        overlayBox.getChildren().clear();
        overlayBox.setVisible(true);
        floatingMenuBtn.setVisible(false);

        Label header = new Label("Select Stage");
        header.setFont(Font.font("Montserrat", 28));
        header.setTextFill(Color.WHITE);

        HBox row1 = new HBox(18); row1.setAlignment(Pos.CENTER);
        HBox row2 = new HBox(18); row2.setAlignment(Pos.CENTER);

        int unlocked = ProgressManager.loadHighestUnlocked();

        for (int i = 1; i <= 5; i++) {
            int lvl = i;
            VBox card = new VBox(8); card.setAlignment(Pos.CENTER);

            Canvas preview = new Canvas(220,120);
            drawLevelPreview(preview.getGraphicsContext2D(), lvl);

            Label lbl = new Label("Stage " + lvl);
            lbl.setTextFill(Color.WHITE); lbl.setFont(Font.font("Segoe UI", 14));

            Button play = makeStyledButton(lvl <= unlocked ? "Play" : "Locked", 120,
                    lvl <= unlocked ? "-fx-background-color: linear-gradient(#66F, #447);" : "-fx-background-color: linear-gradient(#444, #222);");
            play.setDisable(lvl > unlocked);
            if (lvl <= unlocked) play.setOnAction(e -> startSelectedLevel(lvl));

            card.getChildren().addAll(preview, lbl, play);
            if (i <= 3) row1.getChildren().add(card); else row2.getChildren().add(card);
        }

        Button back = makeStyledButton("Back", 120, "-fx-background-color: linear-gradient(#AAA, #888);");
        back.setOnAction(e -> showStartScreen());

        VBox container = new VBox(14, header, row1, row2, back);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: rgba(6,6,6,0.55); -fx-background-radius:12; -fx-padding:16;");
        overlayBox.getChildren().add(container);
    }

    private void drawLevelPreview(GraphicsContext pgc, int level) {
        Stop[] stops = new Stop[]{ new Stop(0, Color.web("#0f0c29")), new Stop(1, Color.web("#302b63")) };
        pgc.setFill(new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE, stops));
        pgc.fillRect(0,0, pgc.getCanvas().getWidth(), pgc.getCanvas().getHeight());

        double gap = 6, startX = 8, startY = 10;
        int cols = 8, rows = 3 + Math.min(2, level-1);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean place = switch(level) {
                    case 1 -> true;
                    case 2 -> ((r+c)%2==0);
                    case 3 -> (r%2==0||c%3==0);
                    case 4 -> ((r+c)%3!=0);
                    default -> ((r+c)%2==0||(c%4==0));
                };
                if (!place) continue;

                double w = (pgc.getCanvas().getWidth() - 2*startX - (cols-1)*gap)/cols;
                double h = 12;
                double x = startX + c*(w + gap);
                double y = startY + r*(h + gap);
                Color fill = r%3==0? Color.web("#66bb6a") : r%3==1? Color.web("#ff8f00") : Color.web("#b71c1c");
                pgc.setFill(fill.deriveColor(0,1,1,0.9));
                pgc.fillRoundRect(x,y,w,h,6,6);
                pgc.setStroke(Color.web("#00000040"));
                pgc.strokeRoundRect(x,y,w,h,6,6);
            }
        }
    }

    private void startSelectedLevel(int level) {
        overlayBox.setVisible(false);
        gameLoop.setStartingLevel(level);
        gameLoop.resetAll();
        gameLoop.start();
        floatingMenuBtn.setVisible(true);
        if (soundManager != null) soundManager.playBackground("/sounds/bg_loop.mp3", true);
        root.requestFocus();
    }

    public void restartGame() {
        overlayBox.setVisible(false);
        gameLoop.resetAll();
        gameLoop.start();
        floatingMenuBtn.setVisible(true);
        if (soundManager != null) soundManager.playBackground("/sounds/bg_loop.mp3", true);
        root.requestFocus();
    }

    public void showInGameMenu() {
        overlayBox.getChildren().clear();
        overlayBox.setVisible(true);
        gameLoop.setPaused(true);

        Label m = new Label("PAUSE");
        m.setFont(Font.font("Montserrat", 26));
        m.setTextFill(Color.WHITE);

        Button resume = makeStyledButton("Resume", 160, "-fx-background-color: linear-gradient(#6EE, #29A);");
        resume.setOnAction(e -> { overlayBox.setVisible(false); gameLoop.setPaused(false); root.requestFocus(); });

        Button restart = makeStyledButton("Restart", 160, "-fx-background-color: linear-gradient(#FFB86B, #FF7A45);");
        restart.setOnAction(e -> { overlayBox.setVisible(false); gameLoop.resetAll(); gameLoop.start(); root.requestFocus(); });

        Button toMenu = makeStyledButton("Main Menu", 160, "-fx-background-color: linear-gradient(#AAA, #777);");
        toMenu.setOnAction(e -> { overlayBox.setVisible(false); gameLoop.stop(); showStartScreen(); });

        VBox box = new VBox(12, m, resume, restart, toMenu);
        box.setAlignment(Pos.CENTER);
        box.setMinWidth(320);
        box.setStyle("-fx-background-color: rgba(6,6,6,0.6); -fx-background-radius:12; -fx-padding:16;");
        overlayBox.getChildren().add(box);
    }

    // ---------------- GAME OVER (complete, polished) ----------------
    public void showGameOver(int finalScore) {
        overlayBox.getChildren().clear();
        if (soundManager != null) {
            soundManager.stopBackground();
            soundManager.playGameOver();
        }

        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);
        card.setMinWidth(480);
        card.setStyle(
                "-fx-background-color: linear-gradient(#171717, #1f1f2b);" +
                        " -fx-background-radius: 14; -fx-padding: 22; -fx-border-color: rgba(255,255,255,0.035); -fx-border-radius:14;"
        );

        Label title = new Label("GAME OVER");
        title.setFont(javafx.scene.text.Font.font("Montserrat", 36));
        title.setTextFill(javafx.scene.paint.Color.web("#FFDDDD"));
        javafx.scene.effect.DropShadow ds = new javafx.scene.effect.DropShadow(18, javafx.scene.paint.Color.color(0.8, 0.15, 0.15, 0.45));
        title.setEffect(ds);

        Label scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(javafx.scene.text.Font.font("Segoe UI Semibold", 22));
        scoreLabel.setTextFill(javafx.scene.paint.Color.web("#FFFFFFEE"));

        IntegerProperty animScore = new SimpleIntegerProperty(0);
        scoreLabel.textProperty().bind(animScore.asString("Score: %d"));
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(animScore, 0)),
                new KeyFrame(Duration.millis(Math.min(1400, 300 + finalScore)), new KeyValue(animScore, finalScore))
        );
        t.play();

        Label note = new Label("Nice try — enter your name to save your score!");
        note.setFont(javafx.scene.text.Font.font("Segoe UI", 12));
        note.setTextFill(javafx.scene.paint.Color.web("#CCCCCC"));

        HBox saveRow = new HBox(8);
        saveRow.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter name (max 20 chars)");
        nameField.setPrefWidth(220);

        Button saveBtn = makeStyledButton("Save Score", 140, "-fx-background-color: linear-gradient(#6EE,#29A);");
        saveBtn.setOnAction(e -> {
            String name = nameField.getText();
            if (name == null) name = "Player";
            name = name.trim();
            if (name.length() > 20) name = name.substring(0, 20);
            try {
                HighScoreManager.saveScore(name, finalScore);
                saveBtn.setDisable(true);
                nameField.setEditable(false);
                note.setText("Saved! You can view High Scores from the main menu.");
            } catch (IOException ex) {
                note.setText("Save failed: " + ex.getMessage());
                note.setTextFill(javafx.scene.paint.Color.web("#FF8888"));
            }
        });

        saveRow.getChildren().addAll(nameField, saveBtn);

        Button playAgain = makeStyledButton("Play Again", 140, "-fx-background-color: linear-gradient(#3AA, #157);");
        playAgain.setOnAction(e -> {
            overlayBox.setVisible(false);
            gameLoop.resetAll();
            gameLoop.start();
            floatingMenuBtn.setVisible(true);
            if (soundManager != null) soundManager.playBackground("/sounds/bg_loop.mp3", true);
            root.requestFocus();
        });

        Button menu = makeStyledButton("Main Menu", 140, "-fx-background-color: linear-gradient(#AAA,#777);");
        menu.setOnAction(e -> {
            overlayBox.getChildren().clear();
            gameLoop.stop();
            showStartScreen();
        });

        Button exit = makeStyledButton("Exit", 100, "-fx-background-color: linear-gradient(#555,#333);");
        exit.setOnAction(e -> {
            if (root != null && root.getScene() != null && root.getScene().getWindow() != null) {
                ((Stage) root.getScene().getWindow()).close();
            }
        });

        HBox actions = new HBox(12, playAgain, menu, exit);
        actions.setAlignment(Pos.CENTER);

        VBox topBox = new VBox(6);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-padding: 8 6 6 6; -fx-background-radius:8; -fx-background-color: rgba(255,255,255,0.02);");

        Label topHeader = new Label("Top 5 Scores");
        topHeader.setFont(javafx.scene.text.Font.font("Segoe UI Semibold", 13));
        topHeader.setTextFill(javafx.scene.paint.Color.web("#FFDDAA"));
        topBox.getChildren().add(topHeader);

        try {
            List<HighScoreManager.Entry> top = HighScoreManager.loadTop(5);
            if (top.isEmpty()) {
                Label none = new Label("No scores yet.");
                none.setFont(javafx.scene.text.Font.font("Segoe UI", 12));
                none.setTextFill(javafx.scene.paint.Color.web("#CCCCCC"));
                topBox.getChildren().add(none);
            } else {
                for (int i = 0; i < top.size(); i++) {
                    HighScoreManager.Entry e = top.get(i);
                    Label l = new Label((i+1) + ". " + e.name + " — " + e.score);
                    l.setFont(javafx.scene.text.Font.font("Segoe UI", 12));
                    l.setTextFill(javafx.scene.paint.Color.web("#FFFFFF"));
                    topBox.getChildren().add(l);
                }
            }
        } catch (IOException ex) {
            Label err = new Label("Couldn't load top scores.");
            err.setTextFill(javafx.scene.paint.Color.web("#FF8888"));
            topBox.getChildren().add(err);
        }

        card.getChildren().addAll(title, scoreLabel, note, saveRow, actions, topBox);

        // entrance animation
        card.setScaleX(0.9); card.setScaleY(0.9); card.setOpacity(0);
        overlayBox.getChildren().add(card);
        overlayBox.setVisible(true);
        floatingMenuBtn.setVisible(false);

        Timeline entrance = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(card.opacityProperty(), 0),
                        new KeyValue(card.scaleXProperty(), 0.9),
                        new KeyValue(card.scaleYProperty(), 0.9)
                ),
                new KeyFrame(Duration.millis(360),
                        new KeyValue(card.opacityProperty(), 1),
                        new KeyValue(card.scaleXProperty(), 1.0),
                        new KeyValue(card.scaleYProperty(), 1.0)
                )
        );
        entrance.play();

        Platform.runLater(() -> nameField.requestFocus());
    }

    // ---------------- LEVEL COMPLETE ----------------
    public void showLevelComplete(int finalScore) {
        overlayBox.getChildren().clear();
        if (soundManager != null) soundManager.stopBackground();

        Label t = new Label("LEVEL COMPLETE!");
        t.setFont(Font.font("Montserrat", 28));
        t.setTextFill(Color.web("#FFFFFFEE"));

        Label sc = new Label("Final Score: " + finalScore);
        sc.setTextFill(Color.WHITE);

        TextField nameField = new TextField();
        nameField.setPromptText("Player");
        nameField.setMaxWidth(220);

        Button save = makeStyledButton("Save Score", 140, "-fx-background-color: linear-gradient(#6EE,#29A);");
        save.setOnAction(e -> {
            try {
                HighScoreManager.saveScore(nameField.getText(), finalScore);
                save.setDisable(true);
            } catch (IOException ex) {
                infoLabel.setText("Save failed: " + ex.getMessage());
            }
        });

        Button playAgain = makeStyledButton("Play Again", 140, "-fx-background-color: linear-gradient(#6EE,#29A);");
        playAgain.setOnAction(e -> restartGame());

        Button menu = makeStyledButton("Main Menu", 140, "-fx-background-color: linear-gradient(#AAA,#777);");
        menu.setOnAction(e -> showStartScreen());

        HBox controls = new HBox(10, playAgain, menu, exitBtn);
        controls.setAlignment(Pos.CENTER);

        VBox box = new VBox(12, t, sc, new HBox(8, nameField, save), controls);
        box.setAlignment(Pos.CENTER);
        box.setMinWidth(460);
        box.setStyle("-fx-background-color: rgba(6,6,6,0.6); -fx-background-radius:12; -fx-padding:16;");

        overlayBox.getChildren().add(box);
        overlayBox.setVisible(true);
        floatingMenuBtn.setVisible(false);

        int unlocked = ProgressManager.loadHighestUnlocked();
        if (unlocked < 5) ProgressManager.saveHighestUnlocked(unlocked + 1);
    }

    private void renderInitialBackground() {
        drawBackground();
        gc.setFill(Color.web("#FFFFFF", 0.04));
        gc.fillRect(0, 0, Constants.WIDTH, 120);
    }

    public void drawBackground() {
        Stop[] stops = new Stop[]{ new Stop(0, Color.web("#091238")), new Stop(1, Color.web("#24406b")) };
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        gc.setFill(lg);
        gc.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

        RadialGradient rg = new RadialGradient(0, 0, Constants.WIDTH/2.0, Constants.HEIGHT/2.0,
                Math.max(Constants.WIDTH, Constants.HEIGHT)/1.1, false, CycleMethod.NO_CYCLE,
                new Stop[]{ new Stop(0, Color.color(0,0,0,0)), new Stop(1, Color.color(0,0,0,0.25)) });
        gc.setFill(rg);
        gc.fillRect(0,0, Constants.WIDTH, Constants.HEIGHT);

        gc.setFill(Color.web("#ffffff", 0.03));
        gc.fillRect(0, Constants.HEIGHT - 80, Constants.WIDTH, 80);
    }

    public static void main(String[] args) { launch(args); }
}
