import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationRepository {
    private static final String FILE_NAME = "reservations.txt";

    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        for (String line : FileStorage.readLines(FILE_NAME)) {
            if (line.isBlank()) {
                continue;
            }
            Reservation reservation = Reservation.fromFileLine(line);
            if (reservation != null) {
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public Optional<Reservation> getById(String id) {
        return getAll().stream().filter(r -> r.getId().equalsIgnoreCase(id)).findFirst();
    }

    public List<Reservation> getByCustomerId(String customerId) {
        return getAll().stream()
                .filter(r -> r.getCustomerId().equalsIgnoreCase(customerId))
                .toList();
    }

    public void create(Reservation reservation) {
        List<String> lines = FileStorage.readLines(FILE_NAME);
        lines.add(reservation.toFileLine());
        FileStorage.writeLines(FILE_NAME, lines);
    }

    public boolean update(Reservation updatedReservation) {
        List<Reservation> reservations = getAll();
        boolean updated = false;
        for (Reservation reservation : reservations) {
            if (reservation.getId().equalsIgnoreCase(updatedReservation.getId())) {
                reservation.setCustomerId(updatedReservation.getCustomerId());
                reservation.setTrainNumber(updatedReservation.getTrainNumber());
                reservation.setBookingDetails(updatedReservation.getBookingDetails());
                updated = true;
                break;
            }
        }
        if (updated) {
            FileStorage.writeLines(FILE_NAME, reservations.stream().map(Reservation::toFileLine).toList());
        }
        return updated;
    }

    public boolean delete(String id) {
        List<Reservation> reservations = getAll();
        boolean removed = reservations.removeIf(r -> r.getId().equalsIgnoreCase(id));
        if (removed) {
            FileStorage.writeLines(FILE_NAME, reservations.stream().map(Reservation::toFileLine).toList());
        }
        return removed;
    }
}
