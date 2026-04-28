import java.util.List;

public class SettingsRepository {
    private static final String FILE_NAME = "settings.txt";

    public UserSettings read() {
        List<String> lines = FileStorage.readLines(FILE_NAME);
        if (lines.isEmpty() || lines.get(0).isBlank()) {
            UserSettings defaults = UserSettings.defaultSettings();
            create(defaults);
            return defaults;
        }
        return UserSettings.fromFileLine(lines.get(0));
    }

    public void create(UserSettings settings) {
        FileStorage.writeLines(FILE_NAME, List.of(settings.toFileLine()));
    }

    public void update(UserSettings settings) {
        FileStorage.writeLines(FILE_NAME, List.of(settings.toFileLine()));
    }

    public void delete() {
        FileStorage.writeLines(FILE_NAME, List.of());
    }
}
