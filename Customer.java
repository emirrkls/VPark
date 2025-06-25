import java.io.Serializable;
import java.util.Objects;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static long nextIdSuffix = 1;
    protected static final String ID_PREFIX = "CUST-"; // Make it protected or public if VehiclePark needs it for parsing

    private final String customerId;
    private String name;
    private String contactInfo;

    public Customer(String name, String contactInfo) {
        this.customerId = ID_PREFIX + nextIdSuffix++;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public static void updateNextIdSuffix(long highestKnownIdSuffix) {
        nextIdSuffix = Math.max(1L, highestKnownIdSuffix + 1);
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }
    public void setName(String name) { this.name = name; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    @Override
    public String toString() {
        return "Customer ID: " + customerId +
               "\n  Name: " + name +
               "\n  Contact: " + contactInfo;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId.equals(customer.customerId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}