import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Train {
    private String trainNumber;
    private String destination;
    private LocalDate date;
    private LocalTime departureTime;

    public Train(String trainNumber, String destination, LocalDate date, LocalTime departureTime) {
        this.trainNumber = trainNumber;
        this.destination = destination;
        this.date = date;
        this.departureTime = departureTime;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public String toFileLine() {
        return String.join("|", trainNumber, sanitize(destination), date.toString(), departureTime.toString());
    }

    public static Train fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
            return null;
        }
        return new Train(parts[0], parts[1], LocalDate.parse(parts[2]), LocalTime.parse(parts[3]));
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace("|", "/");
    }

    @Override
    public String toString() {
        return "Train No: " + trainNumber
                + ", Destination: " + destination
                + ", Date: " + date
                + ", Departure: " + departureTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Train)) {
            return false;
        }
        Train train = (Train) o;
        return Objects.equals(trainNumber, train.trainNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainNumber);
    }
}
