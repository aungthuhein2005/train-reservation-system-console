import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrainRepository {
    private static final String FILE_NAME = "trains.txt";

    public List<Train> getAll() {
        List<Train> trains = new ArrayList<>();
        for (String line : FileStorage.readLines(FILE_NAME)) {
            if (line.isBlank()) {
                continue;
            }
            Train train = Train.fromFileLine(line);
            if (train != null) {
                trains.add(train);
            }
        }
        return trains;
    }

    public Optional<Train> getByTrainNumber(String trainNumber) {
        return getAll().stream().filter(t -> t.getTrainNumber().equalsIgnoreCase(trainNumber)).findFirst();
    }

    public List<Train> search(String destination, LocalDate date) {
        return getAll().stream()
                .filter(t -> t.getDestination().equalsIgnoreCase(destination) && t.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public void create(Train train) {
        List<String> lines = FileStorage.readLines(FILE_NAME);
        lines.add(train.toFileLine());
        FileStorage.writeLines(FILE_NAME, lines);
    }

    public boolean update(Train updatedTrain) {
        List<Train> trains = getAll();
        boolean updated = false;
        for (Train train : trains) {
            if (train.getTrainNumber().equalsIgnoreCase(updatedTrain.getTrainNumber())) {
                train.setDestination(updatedTrain.getDestination());
                train.setDate(updatedTrain.getDate());
                train.setDepartureTime(updatedTrain.getDepartureTime());
                updated = true;
                break;
            }
        }
        if (updated) {
            FileStorage.writeLines(FILE_NAME, trains.stream().map(Train::toFileLine).toList());
        }
        return updated;
    }

    public boolean delete(String trainNumber) {
        List<Train> trains = getAll();
        boolean removed = trains.removeIf(t -> t.getTrainNumber().equalsIgnoreCase(trainNumber));
        if (removed) {
            FileStorage.writeLines(FILE_NAME, trains.stream().map(Train::toFileLine).toList());
        }
        return removed;
    }
}
