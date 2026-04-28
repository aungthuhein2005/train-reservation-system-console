import java.util.Objects;

public class Customer {
    private String id;
    private String name;
    private String contact;

    public Customer(String id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String toFileLine() {
        return String.join("|", id, sanitize(name), sanitize(contact));
    }

    public static Customer fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 3) {
            return null;
        }
        return new Customer(parts[0], parts[1], parts[2]);
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace("|", "/");
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Contact: " + contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
