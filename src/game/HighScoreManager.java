package game;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HighScoreManager {
    private static final String FILE_NAME = "brickbreaker_highscores.txt";
    private static final Path FILE_PATH = Paths.get(System.getProperty("user.home"), FILE_NAME);
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static class Entry {
        public final String name;
        public final int score;
        public final String timestamp;

        public Entry(String name, int score, String timestamp) {
            this.name = name;
            this.score = score;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return name + " - " + score + " (" + timestamp + ")";
        }
    }

    private static void ensureFile() throws IOException {
        if (!Files.exists(FILE_PATH)) {
            Files.createFile(FILE_PATH);
        }
    }

    public static void saveScore(String name, int score) throws IOException {
        if (name == null || name.trim().isEmpty()) name = "Player";
        String ts = LocalDateTime.now().format(TF);
        ensureFile();
        String line = escape(name.trim()) + "|" + score + "|" + ts;
        try (BufferedWriter w = Files.newBufferedWriter(FILE_PATH, StandardOpenOption.APPEND)) {
            w.write(line);
            w.newLine();
        }
    }

    public static List<Entry> loadAll() throws IOException {
        ensureFile();
        List<Entry> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(FILE_PATH)) {
            String ln;
            while ((ln = r.readLine()) != null) {
                String[] parts = ln.split("\\|", 3);
                if (parts.length >= 3) {
                    String name = unescape(parts[0]);
                    int sc;
                    try { sc = Integer.parseInt(parts[1]); } catch (NumberFormatException e) { continue; }
                    out.add(new Entry(name, sc, parts[2]));
                }
            }
        }
        return out;
    }

    public static List<Entry> loadTop(int n) throws IOException {
        List<Entry> all = loadAll();
        return all.stream()
                .sorted((a,b) -> Integer.compare(b.score, a.score))
                .limit(n)
                .collect(Collectors.toList());
    }

    public static void clearAll() throws IOException {
        ensureFile();
        Files.write(FILE_PATH, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String escape(String s) { return s.replace("|", "\\|"); }
    private static String unescape(String s) { return s.replace("\\|", "|"); }
    public static Path getFilePath() { return FILE_PATH; }
}




