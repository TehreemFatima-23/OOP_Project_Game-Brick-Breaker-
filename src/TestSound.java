import javafx.application.Application;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

public class TestSound extends Application {
    @Override
    public void start(Stage stage) {
        try {
            AudioClip clip = new AudioClip(getClass().getResource("/sounds/brick_hit.wav").toString());
            clip.play();
            System.out.println("✅ Sound should play now!");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
