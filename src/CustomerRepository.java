import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRepository {
    private static final String FILE_NAME = "customers.txt";

    public List<Customer> getAll() {
        List<Customer> customers = new ArrayList<>();
        for (String line : FileStorage.readLines(FILE_NAME)) {
            if (line.isBlank()) {
                continue;
            }
            Customer customer = Customer.fromFileLine(line);
            if (customer != null) {
                customers.add(customer);
            }
        }
        return customers;
    }

    public Optional<Customer> getById(String id) {
        return getAll().stream().filter(c -> c.getId().equalsIgnoreCase(id)).findFirst();
    }

    public void create(Customer customer) {
        List<String> lines = FileStorage.readLines(FILE_NAME);
        lines.add(customer.toFileLine());
        FileStorage.writeLines(FILE_NAME, lines);
    }

    public boolean update(Customer updatedCustomer) {
        List<Customer> customers = getAll();
        boolean updated = false;
        for (Customer customer : customers) {
            if (customer.getId().equalsIgnoreCase(updatedCustomer.getId())) {
                customer.setName(updatedCustomer.getName());
                customer.setContact(updatedCustomer.getContact());
                updated = true;
                break;
            }
        }
        if (updated) {
            FileStorage.writeLines(FILE_NAME, customers.stream().map(Customer::toFileLine).toList());
        }
        return updated;
    }

    public boolean delete(String id) {
        List<Customer> customers = getAll();
        boolean removed = customers.removeIf(c -> c.getId().equalsIgnoreCase(id));
        if (removed) {
            FileStorage.writeLines(FILE_NAME, customers.stream().map(Customer::toFileLine).toList());
        }
        return removed;
    }
}
