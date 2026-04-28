public class UserSettings {
    private String defaultCustomerName;
    private String defaultCustomerContact;
    private boolean showDashboardOnStart;

    public UserSettings(String defaultCustomerName, String defaultCustomerContact, boolean showDashboardOnStart) {
        this.defaultCustomerName = defaultCustomerName;
        this.defaultCustomerContact = defaultCustomerContact;
        this.showDashboardOnStart = showDashboardOnStart;
    }

    public static UserSettings defaultSettings() {
        return new UserSettings("", "", true);
    }

    public String getDefaultCustomerName() {
        return defaultCustomerName;
    }

    public void setDefaultCustomerName(String defaultCustomerName) {
        this.defaultCustomerName = defaultCustomerName;
    }

    public String getDefaultCustomerContact() {
        return defaultCustomerContact;
    }

    public void setDefaultCustomerContact(String defaultCustomerContact) {
        this.defaultCustomerContact = defaultCustomerContact;
    }

    public boolean isShowDashboardOnStart() {
        return showDashboardOnStart;
    }

    public void setShowDashboardOnStart(boolean showDashboardOnStart) {
        this.showDashboardOnStart = showDashboardOnStart;
    }

    public String toFileLine() {
        return String.join("|",
                sanitize(defaultCustomerName),
                sanitize(defaultCustomerContact),
                Boolean.toString(showDashboardOnStart));
    }

    public static UserSettings fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 3) {
            return defaultSettings();
        }
        return new UserSettings(parts[0], parts[1], Boolean.parseBoolean(parts[2]));
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace("|", "/");
    }
}
