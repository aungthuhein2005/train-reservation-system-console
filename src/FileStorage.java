import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final Path DATA_DIR = Paths.get("data");

    public static List<String> readLines(String fileName) {
        try {
            ensureDataDir();
            Path filePath = DATA_DIR.resolve(fileName);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void writeLines(String fileName, List<String> lines) {
        try {
            ensureDataDir();
            Path filePath = DATA_DIR.resolve(fileName);
            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Failed to write file " + fileName + ": " + e.getMessage());
        }
    }

    public static void appendLine(String fileName, String line) {
        try {
            ensureDataDir();
            Path filePath = DATA_DIR.resolve(fileName);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            Files.write(filePath, List.of(line), java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Failed to append file " + fileName + ": " + e.getMessage());
        }
    }

    private static void ensureDataDir() throws IOException {
        if (!Files.exists(DATA_DIR)) {
            Files.createDirectories(DATA_DIR);
        }
    }
}
