import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class VehiclePark implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Vehicle> allVehicles;
    private List<Customer> registeredCustomers;

    public VehiclePark() {
        this.allVehicles = new ArrayList<>();
        this.registeredCustomers = new ArrayList<>();
    }

    // --- Vehicle Management Methods ---
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            System.err.println("Error: Cannot add a null vehicle.");
            return false;
        }
        if (findVehicleByPlateNumber(vehicle.getPlateNumber()).isPresent()) { // Check by plate first
            System.err.println("Error: Vehicle with Plate " + vehicle.getPlateNumber() + " already exists.");
            return false;
        }
        // Since ID is auto-generated and should be unique by design with static counters,
        // checking for ID collision is mostly a safeguard against logic errors elsewhere.
        if (findVehicleById(vehicle.getId()).isPresent()){
             System.err.println("Error: Vehicle with ID " + vehicle.getId() + " already exists (ID collision!). This should not happen with sequential IDs.");
             return false;
        }
        this.allVehicles.add(vehicle);
        System.out.println("Vehicle added successfully: ID=" + vehicle.getId() + ", Plate=" + vehicle.getPlateNumber() + " (" + vehicle.getClass().getSimpleName() + ")");
        return true;
    }

    public boolean removeVehicle(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            System.err.println("Error: Vehicle ID cannot be null or empty for removal.");
            return false;
        }
        Optional<Vehicle> vehicleToRemoveOpt = findVehicleById(vehicleId);
        if (vehicleToRemoveOpt.isPresent()) {
            Vehicle vehicleToRemove = vehicleToRemoveOpt.get();
            if (vehicleToRemove.isRented()) {
                System.err.println("Error: Cannot remove vehicle " + vehicleId + ". It is currently rented.");
                return false;
            }
            if (!vehicleToRemove.getBookings().isEmpty()) {
                 System.err.println("Warning: Vehicle " + vehicleId + " has active bookings. These bookings will remain associated with a removed vehicle if not handled.");
                 // For a real system, you might want to prevent removal or auto-cancel bookings.
            }
            this.allVehicles.remove(vehicleToRemove);
            System.out.println("Vehicle removed successfully: " + vehicleToRemove.getPlateNumber() + " (ID: " + vehicleId + ")");
            return true;
        } else {
            System.err.println("Error: Vehicle with ID " + vehicleId + " not found for removal.");
            return false;
        }
    }

    public Optional<Vehicle> findVehicleById(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) return Optional.empty();
        for (Vehicle v : allVehicles) {
            if (v.getId().equals(vehicleId.trim())) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    public Optional<Vehicle> findVehicleByPlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) return Optional.empty();
        for (Vehicle v : allVehicles) {
            if (v.getPlateNumber().equalsIgnoreCase(plateNumber.trim())) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    public void displayAllVehicles() {
        if (allVehicles.isEmpty()) {
            System.out.println("No vehicles currently in the system.");
            return;
        }
        System.out.println("\n--- All Vehicles in VPark ---");
        for (Vehicle v : allVehicles) {
            System.out.println("------------------------------");
            System.out.println(v.toString());
            String availabilityStatus;
            try {
                Date now = new Date();
                // Check for immediate availability (e.g., within the next hour to avoid date precision issues)
                Date soon = new Date(now.getTime() + 1 * 60 * 1000); // 1 minute from now
                availabilityStatus = v.isAvailable(now, soon) ? "Available Now" : "Not Available Now";
            } catch (InvalidDateException e) {
                availabilityStatus = "Availability Unknown (Error: " + e.getMessage() + ")";
            }
            System.out.println("  Current Status: " + (v.isRented() ? "Rented" : availabilityStatus));
        }
        System.out.println("------------------------------");
    }

    public void displayAvailableVehicles(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            System.err.println("Invalid date range provided for displaying available vehicles.");
            return;
        }
        System.out.println("\nSearching for available vehicles from: " + startDate.toGMTString() + " to " + endDate.toGMTString());
        List<Vehicle> availableVehicles = new ArrayList<>();
        for (Vehicle v : allVehicles) {
            try {
                if (v.isAvailable(startDate, endDate)) {
                    availableVehicles.add(v);
                }
            } catch (InvalidDateException e) {
                // This exception in v.isAvailable usually means the dates passed to it were bad,
                // but we checked startDate/endDate already. Could be an internal issue in isAvailable logic.
                System.err.println("Error checking availability for vehicle " + v.getPlateNumber() + " (ID: " + v.getId() + "): " + e.getMessage());
            }
        }

        if (availableVehicles.isEmpty()) {
            System.out.println("No vehicles available for the period: " + startDate.toGMTString() + " to " + endDate.toGMTString());
            return;
        }
        System.out.println("\n--- Available Vehicles (" + startDate.toGMTString() + " - " + endDate.toGMTString() + ") ---");
        for (Vehicle v : availableVehicles) {
            System.out.println("------------------------------");
            System.out.println(v.toString());
        }
        System.out.println("------------------------------");
    }

    public void displayAvailableVehicles(Date startDate, Date endDate, String vehicleTypeClassName) {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            System.err.println("Invalid date range provided.");
            return;
        }
        if (vehicleTypeClassName == null || vehicleTypeClassName.trim().isEmpty()) {
            System.err.println("Vehicle type cannot be empty.");
            return;
        }
        System.out.println("\nSearching for available '" + vehicleTypeClassName + "' vehicles from: " + startDate.toGMTString() + " to " + endDate.toGMTString());
        List<Vehicle> availableVehiclesOfType = new ArrayList<>();
        String searchType = vehicleTypeClassName.trim();

        for (Vehicle v : allVehicles) {
            boolean typeMatch = false;
            // Match against specific class name or general "Car"/"Truck"
            if (v.getClass().getSimpleName().equalsIgnoreCase(searchType)) {
                typeMatch = true;
            } else if (("Car".equalsIgnoreCase(searchType) && v instanceof Car) ||
                       ("Truck".equalsIgnoreCase(searchType) && v instanceof Truck)) {
                typeMatch = true;
            }

            if (typeMatch) {
                try {
                    if (v.isAvailable(startDate, endDate)) {
                        availableVehiclesOfType.add(v);
                    }
                } catch (InvalidDateException e) {
                    System.err.println("Error checking availability for vehicle " + v.getPlateNumber() + " (ID: " + v.getId() + "): " + e.getMessage());
                }
            }
        }

        if (availableVehiclesOfType.isEmpty()) {
            System.out.println("No " + searchType + " vehicles available for the period: " + startDate.toGMTString() + " to " + endDate.toGMTString());
            return;
        }
        System.out.println("\n--- Available " + searchType + " Vehicles (" + startDate.toGMTString() + " - " + endDate.toGMTString() + ") ---");
        for (Vehicle v : availableVehiclesOfType) {
            System.out.println("------------------------------");
            System.out.println(v.toString());
        }
        System.out.println("------------------------------");
    }

    // --- Customer Management Methods ---
    public boolean addCustomer(Customer customer) {
        if (customer == null) {
            System.err.println("Error: Cannot add a null customer.");
            return false;
        }
        if (findCustomerById(customer.getCustomerId()).isPresent()){
           System.err.println("Error: Customer with ID " + customer.getCustomerId() + " already exists (ID collision!). This should not happen with sequential IDs.");
           return false;
        }
        this.registeredCustomers.add(customer);
        System.out.println("Customer added successfully: " + customer.getName() + " (ID: " + customer.getCustomerId() + ")");
        return true;
    }

    public Optional<Customer> findCustomerById(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) return Optional.empty();
        for (Customer c : registeredCustomers) {
            if (c.getCustomerId().equals(customerId.trim())) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    public void displayAllCustomers() {
        if (registeredCustomers.isEmpty()) {
            System.out.println("No customers currently registered in the system.");
            return;
        }
        System.out.println("\n--- All Registered Customers ---");
        for (Customer c : registeredCustomers) {
            System.out.println("------------------------------");
            System.out.println(c.toString());
        }
        System.out.println("------------------------------");
    }

    // --- Booking, Rental, and Other Operations ---
    public void bookVehicle(String vehicleId, String customerId, Date startDate, Date endDate) {
        Optional<Customer> customerOpt = findCustomerById(customerId);
        if (!customerOpt.isPresent()) {
            System.err.println("Booking failed: Customer with ID " + customerId + " not found.");
            return;
        }
        Optional<Vehicle> vehicleOpt = findVehicleById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            System.err.println("Booking failed: Vehicle with ID " + vehicleId + " not found.");
            return;
        }
        Vehicle vehicle = vehicleOpt.get();
        Customer customer = customerOpt.get();
        try {
            System.out.println("Customer " + customer.getName() + " (ID: "+customerId+") attempting to book vehicle " + vehicle.getPlateNumber() + " (ID: "+vehicleId+")...");
            vehicle.bookMe(startDate, endDate);
        } catch (SorryWeDontHaveThatOneException | InvalidDateException e) {
            System.err.println("Booking failed for vehicle " + vehicle.getPlateNumber() + " (ID: "+vehicleId+"): " + e.getMessage());
        }
    }

    public void cancelBooking(String vehicleId, String customerId, Date startDate, Date endDate) {
        Optional<Customer> customerOpt = findCustomerById(customerId);
        if (!customerOpt.isPresent()) {
            System.err.println("Booking cancellation failed: Customer with ID " + customerId + " not found.");
            return;
        }
        Optional<Vehicle> vehicleOpt = findVehicleById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            System.err.println("Booking cancellation failed: Vehicle with ID " + vehicleId + " not found.");
            return;
        }
        Vehicle vehicle = vehicleOpt.get();
        Customer customer = customerOpt.get();
        try {
            System.out.println("Customer " + customer.getName() + " (ID: "+customerId+") attempting to cancel booking for vehicle " +
                               vehicle.getPlateNumber() + " (ID: "+vehicleId+") for period " + startDate.toGMTString() + " to " + endDate.toGMTString() + "...");
            vehicle.cancelMe(startDate, endDate);
        } catch (NoCancellationYouMustPayException | InvalidDateException e) {
            System.err.println("Booking cancellation failed for vehicle " + vehicle.getPlateNumber() + " (ID: "+vehicleId+"): " + e.getMessage());
        }
    }

    public void rentVehicle(String vehicleId, String customerId, Date startDate, Date endDate, String deliveryLocation, String dropOffLocation) {
        Optional<Customer> customerOpt = findCustomerById(customerId);
        if (!customerOpt.isPresent()) {
            System.err.println("Rental failed: Customer with ID " + customerId + " not found.");
            return;
        }
        Optional<Vehicle> vehicleOpt = findVehicleById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            System.err.println("Rental failed: Vehicle with ID " + vehicleId + " not found.");
            return;
        }
        Vehicle vehicle = vehicleOpt.get();
        Customer customer = customerOpt.get();
        try {
            System.out.println("Customer " + customer.getName() + " (ID: "+customerId+") attempting to rent vehicle " + vehicle.getPlateNumber() + " (ID: "+vehicleId+")...");
            vehicle.rentMe(startDate, endDate, deliveryLocation, dropOffLocation);
        } catch (SorryWeDontHaveThatOneException | InvalidDateException e) {
            System.err.println("Rental failed for vehicle " + vehicle.getPlateNumber() + " (ID: "+vehicleId+"): " + e.getMessage());
        }
    }

    public void dropVehicle(String vehicleId, String customerId) {
        Optional<Customer> customerOpt = findCustomerById(customerId);
        if (!customerOpt.isPresent()) {
            System.err.println("Vehicle drop-off failed: Customer with ID " + customerId + " not found.");
            return;
        }
        Optional<Vehicle> vehicleOpt = findVehicleById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            System.err.println("Vehicle drop-off failed: Vehicle with ID " + vehicleId + " not found.");
            return;
        }
        Vehicle vehicle = vehicleOpt.get();
        Customer customer = customerOpt.get();
        try {
            System.out.println("Customer " + customer.getName() + " (ID: "+customerId+") attempting to drop off vehicle " + vehicle.getPlateNumber() + " (ID: "+vehicleId+")...");
            vehicle.dropMe();
        } catch (InvalidDateException e) {
            System.err.println("Vehicle drop-off failed for " + vehicle.getPlateNumber() + " (ID: "+vehicleId+"): " + e.getMessage());
        }
    }

    public void loadVehicle(String vehicleId, String customerId, double amount) {
        Optional<Customer> customerOpt = findCustomerById(customerId);
        if (!customerOpt.isPresent()) {
            System.err.println("Loading vehicle failed: Customer with ID " + customerId + " not found.");
            return;
        }
        Optional<Vehicle> vehicleOpt = findVehicleById(vehicleId);
        if (!vehicleOpt.isPresent()) {
            System.err.println("Loading vehicle failed: Vehicle with ID " + vehicleId + " not found.");
            return;
        }
        Vehicle vehicle = vehicleOpt.get();
        Customer customer = customerOpt.get();
        try {
            System.out.println("Customer " + customer.getName() + " (ID: "+customerId+") attempting to load " + amount +
                               " onto vehicle " + vehicle.getPlateNumber() + " (ID: "+vehicleId+")...");
            vehicle.loadMe(amount);
        } catch (OverWeightException | UnsupportedOperationException e) {
            System.err.println("Loading vehicle " + vehicle.getPlateNumber() + " (ID: "+vehicleId+") failed: " + e.getMessage());
        }
    }

    public void dailyReport(String fileName) {
        File reportFile = new File(fileName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String reportTimestamp = dateFormat.format(new Date());

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile))) {
            writer.println("VPark - Daily System Report");
            writer.println("Generated on: " + reportTimestamp);
            writer.println("============================================\n");

            writer.println("--- ALL VEHICLES (" + allVehicles.size() + ") ---");
            if (allVehicles.isEmpty()) {
                writer.println("No vehicles in the system.");
            } else {
                for (Vehicle v : allVehicles) {
                    writer.println("\n-- Vehicle ID: " + v.getId() + " | Plate: " + v.getPlateNumber() + " | Type: " + v.getClass().getSimpleName());
                    // Replace newlines in vehicle's toString() with indented newlines for better report formatting
                    String vehicleDetails = v.toString().replaceAll("\n", "\n    ");
                    writer.println("  " + vehicleDetails); // First line of vehicle details also indented
                    writer.println("    Currently Rented: " + (v.isRented() ? "Yes" : "No"));
                    if (v.isRented() && v.getCurrentRentalPeriod() != null) {
                        writer.println("      Rental Period: " + v.getCurrentRentalPeriod().getStartDate().toGMTString() +
                                       " to " + v.getCurrentRentalPeriod().getEndDate().toGMTString());
                    }
                    writer.println("    Number of Bookings: " + v.getBookings().size());
                    for (Booking b : v.getBookings()) {
                        writer.println("      Booking: " + b.getStartDate().toGMTString() + " to " + b.getEndDate().toGMTString());
                    }
                }
            }
            writer.println("\n--------------------------------------------\n");

            writer.println("--- RENTED VEHICLES ---");
            List<Vehicle> rentedVehiclesList = new ArrayList<>();
            for (Vehicle v : allVehicles) { if (v.isRented()) rentedVehiclesList.add(v); }
            if (rentedVehiclesList.isEmpty()) {
                writer.println("No vehicles currently rented.");
            } else {
                for (Vehicle v : rentedVehiclesList) {
                    writer.println("\n-- Plate: " + v.getPlateNumber() + " (ID: " + v.getId() + ")");
                    writer.println("   Rented From: " + (v.getCurrentRentalPeriod() != null ? v.getCurrentRentalPeriod().getStartDate().toGMTString() : "N/A"));
                    writer.println("   Rented Until: " + (v.getCurrentRentalPeriod() != null ? v.getCurrentRentalPeriod().getEndDate().toGMTString() : "N/A"));
                }
            }
            writer.println("\n--------------------------------------------\n");

            writer.println("--- BOOKED VEHICLES (Future Bookings) ---");
            List<Vehicle> vehiclesWithBookings = new ArrayList<>();
            for(Vehicle v : allVehicles) { if(!v.getBookings().isEmpty()) vehiclesWithBookings.add(v); }
            if (vehiclesWithBookings.isEmpty()) {
                writer.println("No vehicles have future bookings.");
            } else {
                for (Vehicle v : vehiclesWithBookings) {
                    writer.println("\n-- Plate: " + v.getPlateNumber() + " (ID: " + v.getId() + ")");
                    for (Booking b : v.getBookings()) {
                        writer.println("   Booking: From " + b.getStartDate().toGMTString() + " To " + b.getEndDate().toGMTString());
                    }
                }
            }
            writer.println("\n--------------------------------------------\n");

            writer.println("--- REGISTERED CUSTOMERS (" + registeredCustomers.size() + ") ---");
            if (registeredCustomers.isEmpty()) {
                writer.println("No customers registered.");
            } else {
                for (Customer c : registeredCustomers) {
                    writer.println("\n-- Customer ID: " + c.getCustomerId() + " | Name: " + c.getName());
                    writer.println("   Contact: " + c.getContactInfo());
                }
            }
            writer.println("\n============================================");
            writer.println("End of Report");
            System.out.println("Daily report generated successfully: " + reportFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing daily report to file '" + fileName + "': " + e.getMessage());
        }
    }

    // Getters and Setters for lists (for saving/loading state)
    public List<Vehicle> getAllVehicles() { return new ArrayList<>(allVehicles); }
    public List<Customer> getRegisteredCustomers() { return new ArrayList<>(registeredCustomers); }

    public void setAllVehicles(List<Vehicle> vehicles) {
        if (vehicles != null) {
            this.allVehicles = new ArrayList<>(vehicles); // Use a copy
            if (!this.allVehicles.isEmpty()) {
                long maxIdSuffix = 0;
                for(Vehicle v : this.allVehicles) {
                    try {
                        String idStr = v.getId();
                        if (idStr != null && idStr.startsWith(Vehicle.ID_PREFIX)) {
                            String idNumStr = idStr.substring(Vehicle.ID_PREFIX.length());
                            maxIdSuffix = Math.max(maxIdSuffix, Long.parseLong(idNumStr));
                        }
                    } catch (Exception e) { System.err.println("Warning: Could not parse vehicle ID suffix for " + v.getId() + " during load: " + e.getMessage()); }
                }
                Vehicle.updateNextIdSuffix(maxIdSuffix);
            } else {
                Vehicle.updateNextIdSuffix(0); // Reset if list is empty
            }
        } else {
            this.allVehicles = new ArrayList<>();
            Vehicle.updateNextIdSuffix(0); // Reset if list is null
        }
    }

    public void setRegisteredCustomers(List<Customer> customers) {
        if (customers != null) {
            this.registeredCustomers = new ArrayList<>(customers);
            if(!this.registeredCustomers.isEmpty()){
                long maxIdSuffix = 0;
                for(Customer c : this.registeredCustomers){
                     try {
                        String idStr = c.getCustomerId();
                        if (idStr != null && idStr.startsWith(Customer.ID_PREFIX)) {
                            String idNumStr = idStr.substring(Customer.ID_PREFIX.length());
                            maxIdSuffix = Math.max(maxIdSuffix, Long.parseLong(idNumStr));
                        }
                    } catch (Exception e) { System.err.println("Warning: Could not parse customer ID suffix for " + c.getCustomerId() + " during load: " + e.getMessage());}
                }
                Customer.updateNextIdSuffix(maxIdSuffix);
            } else {
                Customer.updateNextIdSuffix(0);
            }
        } else {
            this.registeredCustomers = new ArrayList<>();
            Customer.updateNextIdSuffix(0);
        }
    }
}