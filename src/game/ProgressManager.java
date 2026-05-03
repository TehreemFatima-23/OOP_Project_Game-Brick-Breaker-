package game;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class ProgressManager {
    private static final String FILE_NAME = "brickbreaker_progress.txt";
    private static final Path FILE_PATH = Paths.get(System.getProperty("user.home"), FILE_NAME);

    public static int loadHighestUnlocked() {
        try {
            if (!Files.exists(FILE_PATH)) return 1;
            List<String> lines = Files.readAllLines(FILE_PATH);
            if (lines.isEmpty()) return 1;
            try { return Math.max(1, Integer.parseInt(lines.get(0).trim())); }
            catch (NumberFormatException e) { return 1; }
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static void saveHighestUnlocked(int lvl) {
        try {
            Files.write(FILE_PATH, String.valueOf(Math.max(1, lvl)).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


