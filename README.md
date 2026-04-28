# Train Reservation System

This is my CS first year JS project: a console-based Train Reservation System.

## Project Overview

- Dashboard, Train, History, and Settings menus
- Search and book trains by destination and date
- CRUD operations for `Customer`, `Train`, and `Reservation`
- Reservation history tracking
- User settings (default customer info and exit option)
- File-based data storage (no external database)

## Tech Used

- Java SE (console application)
- File handling with `.txt` files for persistence

## Project Structure

- `src/Main.java` - Application entry point and menu flow
- `src/Customer.java` - Customer model
- `src/Train.java` - Train model
- `src/Reservation.java` - Reservation model
- `src/UserSettings.java` - Settings model
- `src/FileStorage.java` - Generic file read/write helpers
- `src/CustomerRepository.java` - Customer CRUD
- `src/TrainRepository.java` - Train CRUD + search
- `src/ReservationRepository.java` - Reservation CRUD
- `src/SettingsRepository.java` - Settings persistence
- `data/` - Generated text files for stored records

## How to Run

1. Compile:

```bash
javac src\*.java
```

1. Run:

```bash
java -cp src Main
```

## Notes

- Data is saved in `data/*.txt` files.
- The app seeds a few sample trains on first run.

