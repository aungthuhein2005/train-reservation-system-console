import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {
    private String id;
    private String customerId;
    private String trainNumber;
    private String bookingDetails;
    private LocalDateTime bookedAt;

    public Reservation(String id, String customerId, String trainNumber, String bookingDetails, LocalDateTime bookedAt) {
        this.id = id;
        this.customerId = customerId;
        this.trainNumber = trainNumber;
        this.bookingDetails = bookingDetails;
        this.bookedAt = bookedAt;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getBookingDetails() {
        return bookingDetails;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public void setBookingDetails(String bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    public String toFileLine() {
        return String.join("|",
                id,
                sanitize(customerId),
                sanitize(trainNumber),
                sanitize(bookingDetails),
                bookedAt.toString());
    }

    public static Reservation fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 5) {
            return null;
        }
        return new Reservation(parts[0], parts[1], parts[2], parts[3], LocalDateTime.parse(parts[4]));
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace("|", "/");
    }

    @Override
    public String toString() {
        return "Reservation ID: " + id
                + ", Customer ID: " + customerId
                + ", Train: " + trainNumber
                + ", Details: " + bookingDetails
                + ", Booked At: " + bookedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation)) {
            return false;
        }
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
