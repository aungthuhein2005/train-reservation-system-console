import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final TrainRepository trainRepository = new TrainRepository();
    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final SettingsRepository settingsRepository = new SettingsRepository();
    private UserSettings settings;

    public static void main(String[] args) {
        new Main().start();
    }

    private void start() {
        settings = settingsRepository.read();
        seedInitialTrainData();

        if (settings.isShowDashboardOnStart()) {
            showDashboard();
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> showDashboard();
                case 2 -> trainMenu();
                case 3 -> historyMenu();
                case 4 -> running = settingsMenu();
                default -> System.out.println("Invalid menu option.");
            }
        }
        System.out.println("Goodbye!");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("==== Train Reservation System ====");
        System.out.println("1. Dashboard");
        System.out.println("2. Train");
        System.out.println("3. History");
        System.out.println("4. Settings");
    }

    private void showDashboard() {
        System.out.println();
        System.out.println("==== Dashboard ====");
        System.out.println("Customers: " + customerRepository.getAll().size());
        System.out.println("Trains: " + trainRepository.getAll().size());
        System.out.println("Reservations: " + reservationRepository.getAll().size());
    }

    private void trainMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("==== Train Menu ====");
            System.out.println("1. Search and book train");
            System.out.println("2. Manage trains (CRUD)");
            System.out.println("3. Back");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> searchAndBookTrain();
                case 2 -> manageTrainsCrud();
                case 3 -> back = true;
                default -> System.out.println("Invalid menu option.");
            }
        }
    }

    private void searchAndBookTrain() {
        System.out.println();
        String destination = readNonEmpty("Enter destination: ");
        LocalDate date = readDate("Enter date (yyyy-MM-dd): ");

        List<Train> matches = trainRepository.search(destination, date);
        if (matches.isEmpty()) {
            System.out.println("No trains found for this destination/date.");
            return;
        }

        System.out.println("Available trains:");
        for (int i = 0; i < matches.size(); i++) {
            System.out.println((i + 1) + ". " + matches.get(i));
        }

        int selectedIndex = readInt("Choose train number from list (0 to cancel): ");
        if (selectedIndex == 0) {
            System.out.println("Booking cancelled.");
            return;
        }
        if (selectedIndex < 1 || selectedIndex > matches.size()) {
            System.out.println("Invalid train selection.");
            return;
        }

        Train selectedTrain = matches.get(selectedIndex - 1);
        Customer customer = captureCustomer();
        String details = readNonEmpty("Enter booking details (seat/class): ");

        Reservation reservation = new Reservation(
                UUID.randomUUID().toString(),
                customer.getId(),
                selectedTrain.getTrainNumber(),
                details,
                LocalDateTime.now());
        reservationRepository.create(reservation);

        String historyLine = "Reservation Confirmed | " + reservation.getId() + " | Customer: " + customer.getName()
                + " | Train: " + selectedTrain.getTrainNumber()
                + " | Destination: " + selectedTrain.getDestination()
                + " | Date: " + selectedTrain.getDate()
                + " | BookedAt: " + reservation.getBookedAt();
        FileStorage.appendLine("history.txt", historyLine);

        System.out.println("Reservation confirmed!");
        System.out.println(reservation);
    }

    private Customer captureCustomer() {
        System.out.println();
        System.out.println("Customer info:");
        String suggestedName = settings.getDefaultCustomerName();
        String suggestedContact = settings.getDefaultCustomerContact();

        String namePrompt = suggestedName.isBlank()
                ? "Enter customer name: "
                : "Enter customer name (default: " + suggestedName + "): ";
        String contactPrompt = suggestedContact.isBlank()
                ? "Enter contact (digits, +, -): "
                : "Enter contact (default: " + suggestedContact + "): ";

        System.out.print(namePrompt);
        String nameInput = scanner.nextLine().trim();
        if (nameInput.isBlank()) {
            nameInput = suggestedName;
        }
        while (nameInput.isBlank()) {
            System.out.print(namePrompt);
            nameInput = scanner.nextLine().trim();
            if (nameInput.isBlank()) {
                nameInput = suggestedName;
            }
        }

        String contactInput = "";
        while (true) {
            System.out.print(contactPrompt);
            contactInput = scanner.nextLine().trim();
            if (contactInput.isBlank()) {
                contactInput = suggestedContact;
            }
            if (isValidContact(contactInput)) {
                break;
            }
            System.out.println("Invalid contact format.");
        }

        Customer customer = new Customer(UUID.randomUUID().toString(), nameInput, contactInput);
        customerRepository.create(customer);
        return customer;
    }

    private void manageTrainsCrud() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("==== Train CRUD ====");
            System.out.println("1. Create train");
            System.out.println("2. Read all trains");
            System.out.println("3. Update train");
            System.out.println("4. Delete train");
            System.out.println("5. Back");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> createTrain();
                case 2 -> listTrains();
                case 3 -> updateTrain();
                case 4 -> deleteTrain();
                case 5 -> back = true;
                default -> System.out.println("Invalid menu option.");
            }
        }
    }

    private void createTrain() {
        String trainNumber = readNonEmpty("Train number: ");
        if (trainRepository.getByTrainNumber(trainNumber).isPresent()) {
            System.out.println("Train number already exists.");
            return;
        }
        String destination = readNonEmpty("Destination: ");
        LocalDate date = readDate("Date (yyyy-MM-dd): ");
        LocalTime time = readTime("Departure time (HH:mm): ");

        trainRepository.create(new Train(trainNumber, destination, date, time));
        System.out.println("Train created.");
    }

    private void listTrains() {
        List<Train> trains = trainRepository.getAll();
        if (trains.isEmpty()) {
            System.out.println("No train records.");
            return;
        }
        trains.forEach(System.out::println);
    }

    private void updateTrain() {
        String trainNumber = readNonEmpty("Train number to update: ");
        Train existing = trainRepository.getByTrainNumber(trainNumber).orElse(null);
        if (existing == null) {
            System.out.println("Train not found.");
            return;
        }

        String destination = readNonEmpty("New destination: ");
        LocalDate date = readDate("New date (yyyy-MM-dd): ");
        LocalTime time = readTime("New departure time (HH:mm): ");

        Train updated = new Train(existing.getTrainNumber(), destination, date, time);
        trainRepository.update(updated);
        System.out.println("Train updated.");
    }

    private void deleteTrain() {
        String trainNumber = readNonEmpty("Train number to delete: ");
        if (trainRepository.delete(trainNumber)) {
            System.out.println("Train deleted.");
        } else {
            System.out.println("Train not found.");
        }
    }

    private void historyMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("==== History ====");
            System.out.println("1. View reservation history log");
            System.out.println("2. Reservation CRUD");
            System.out.println("3. Customer CRUD");
            System.out.println("4. Back");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> viewHistoryLog();
                case 2 -> reservationCrudMenu();
                case 3 -> customerCrudMenu();
                case 4 -> back = true;
                default -> System.out.println("Invalid menu option.");
            }
        }
    }

    private void viewHistoryLog() {
        List<String> lines = FileStorage.readLines("history.txt");
        if (lines.isEmpty()) {
            System.out.println("No reservation history yet.");
            return;
        }
        lines.forEach(System.out::println);
    }

    private void reservationCrudMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("==== Reservation CRUD ====");
            System.out.println("1. Read all");
            System.out.println("2. Update");
            System.out.println("3. Delete");
            System.out.println("4. Back");
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> reservationRepository.getAll().forEach(System.out::println);
                case 2 -> updateReservation();
                case 3 -> deleteReservation();
                case 4 -> back = true;
                default -> System.out.println("Invalid menu option.");
            }
        }
    }

    private void updateReservation() {
        String id = readNonEmpty("Reservation ID to update: ");
        Reservation existing = reservationRepository.getById(id).orElse(null);
        if (existing == null) {
            System.out.println("Reservation not found.");
            return;
        }

        String customerId = readNonEmpty("New customer ID: ");
        String trainNumber = readNonEmpty("New train number: ");
        String details = readNonEmpty("New booking details: ");

        Reservation updated = new Reservation(existing.getId(), customerId, trainNumber, details, existing.getBookedAt());
        reservationRepository.update(updated);
        System.out.println("Reservation updated.");
    }

    private void deleteReservation() {
        String id = readNonEmpty("Reservation ID to delete: ");
        if (reservationRepository.delete(id)) {
            System.out.println("Reservation deleted.");
        } else {
            System.out.println("Reservation not found.");
        }
    }

    private void customerCrudMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("==== Customer CRUD ====");
            System.out.println("1. Read all");
            System.out.println("2. Update");
            System.out.println("3. Delete");
            System.out.println("4. Back");
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> customerRepository.getAll().forEach(System.out::println);
                case 2 -> updateCustomer();
                case 3 -> deleteCustomer();
                case 4 -> back = true;
                default -> System.out.println("Invalid menu option.");
            }
        }
    }

    private void updateCustomer() {
        String id = readNonEmpty("Customer ID to update: ");
        Customer existing = customerRepository.getById(id).orElse(null);
        if (existing == null) {
            System.out.println("Customer not found.");
            return;
        }

        String name = readNonEmpty("New customer name: ");
        String contact;
        while (true) {
            contact = readNonEmpty("New contact: ");
            if (isValidContact(contact)) {
                break;
            }
            System.out.println("Invalid contact format.");
        }

        customerRepository.update(new Customer(id, name, contact));
        System.out.println("Customer updated.");
    }

    private void deleteCustomer() {
        String id = readNonEmpty("Customer ID to delete: ");
        if (customerRepository.delete(id)) {
            System.out.println("Customer deleted.");
        } else {
            System.out.println("Customer not found.");
        }
    }

    private boolean settingsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("==== Settings ====");
            System.out.println("1. View settings");
            System.out.println("2. Update default customer info");
            System.out.println("3. Toggle dashboard on startup");
            System.out.println("4. Reset settings");
            System.out.println("5. Exit app");
            System.out.println("6. Back");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> printSettings();
                case 2 -> updateDefaultCustomerSettings();
                case 3 -> toggleDashboardOnStart();
                case 4 -> resetSettings();
                case 5 -> {
                    return false;
                }
                case 6 -> back = true;
                default -> System.out.println("Invalid menu option.");
            }
        }
        return true;
    }

    private void printSettings() {
        System.out.println("Default name: " + settings.getDefaultCustomerName());
        System.out.println("Default contact: " + settings.getDefaultCustomerContact());
        System.out.println("Show dashboard on startup: " + settings.isShowDashboardOnStart());
    }

    private void updateDefaultCustomerSettings() {
        String name = readNonEmpty("Default customer name: ");
        String contact;
        while (true) {
            contact = readNonEmpty("Default contact: ");
            if (isValidContact(contact)) {
                break;
            }
            System.out.println("Invalid contact format.");
        }

        settings.setDefaultCustomerName(name);
        settings.setDefaultCustomerContact(contact);
        settingsRepository.update(settings);
        System.out.println("Settings updated.");
    }

    private void toggleDashboardOnStart() {
        settings.setShowDashboardOnStart(!settings.isShowDashboardOnStart());
        settingsRepository.update(settings);
        System.out.println("Show dashboard on startup is now: " + settings.isShowDashboardOnStart());
    }

    private void resetSettings() {
        settingsRepository.delete();
        settings = UserSettings.defaultSettings();
        settingsRepository.create(settings);
        System.out.println("Settings reset to defaults.");
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("Value cannot be empty.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
            }
        }
    }

    private LocalTime readTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return LocalTime.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format.");
            }
        }
    }

    private boolean isValidContact(String value) {
        return value != null && value.matches("[+0-9\\- ]{5,20}");
    }

    private void seedInitialTrainData() {
        if (!trainRepository.getAll().isEmpty()) {
            return;
        }
        trainRepository.create(new Train("TR-1001", "Yangon", LocalDate.now().plusDays(1), LocalTime.of(9, 30)));
        trainRepository.create(new Train("TR-1002", "Mandalay", LocalDate.now().plusDays(1), LocalTime.of(14, 0)));
        trainRepository.create(new Train("TR-1003", "Naypyitaw", LocalDate.now().plusDays(2), LocalTime.of(7, 45)));
    }
}
