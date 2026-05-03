package game;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

public class HighScoreScreen {

    public static void showHighScores(GameApp app) {
        VBox overlay = app.getOverlayBox();
        overlay.getChildren().clear();

        Label title = new Label("HIGH SCORES");
        title.setFont(javafx.scene.text.Font.font("Montserrat", 28));
        title.setTextFill(Color.web("#FFF7D6"));
        title.setEffect(new DropShadow(6, Color.color(0,0,0,0.6)));

        VBox listBox = new VBox(8);
        listBox.setAlignment(Pos.CENTER);

        try {
            // load top 5 highest scores
            List<HighScoreManager.Entry> top = HighScoreManager.loadTop(5);
            if (top.isEmpty()) {
                Label none = new Label("No scores yet. Be the first!");
                none.setTextFill(Color.web("#CCCCCC"));
                none.setFont(javafx.scene.text.Font.font("Segoe UI", 14));
                listBox.getChildren().add(none);
            } else {
                for (int i = 0; i < top.size(); i++) {
                    HighScoreManager.Entry e = top.get(i);
                    Label l = new Label((i + 1) + ". " + e.name + "  —  " + e.score);
                    l.setTextFill(Color.web("#FFFFFF"));
                    l.setFont(javafx.scene.text.Font.font("Segoe UI", 16));
                    listBox.getChildren().add(l);
                }
            }
        } catch (IOException ex) {
            Label err = new Label("Failed to load scores: " + ex.getMessage());
            err.setTextFill(Color.web("#FF8888"));
            listBox.getChildren().add(err);
        }

        // Buttons
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(160);
        backBtn.setStyle("-fx-background-radius:8; -fx-background-color: linear-gradient(#6EE, #29A); -fx-text-fill: white; -fx-font-weight:bold;");
        backBtn.setOnAction(e -> {
            overlay.getChildren().clear();
            app.showStartScreen();
        });
        backBtn.setOnMouseEntered(e -> backBtn.setOpacity(0.92));
        backBtn.setOnMouseExited(e -> backBtn.setOpacity(1.0));

        Button clearBtn = new Button("Clear All Scores");
        clearBtn.setPrefWidth(160);
        clearBtn.setStyle("-fx-background-radius:8; -fx-background-color: linear-gradient(#FF7A45,#FFB86B); -fx-text-fill: white; -fx-font-weight:bold;");
        clearBtn.setOnAction(e -> {
            try {
                HighScoreManager.clearAll();
                showHighScores(app); // refresh
            } catch (IOException ex) {
                overlay.getChildren().add(new Label("Failed to clear scores: " + ex.getMessage()));
            }
        });

        HBox controls = new HBox(12, backBtn, clearBtn);
        controls.setAlignment(Pos.CENTER);

        VBox box = new VBox(16, title, listBox, controls);
        box.setAlignment(Pos.CENTER);
        box.setMinWidth(420);
        box.setStyle("-fx-background-color: rgba(18,18,18,0.88); -fx-background-radius: 12; -fx-padding: 18; -fx-border-color: rgba(255,255,255,0.03); -fx-border-radius:12;");

        overlay.getChildren().add(box);
        overlay.setVisible(true);
        app.getFloatingMenuBtn().setVisible(false);

    }
}
