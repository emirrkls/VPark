import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit; // For date difference calculation

public abstract class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    private static long nextIdSuffix = 1; // Static counter for all Vehicle instances
    protected static final String ID_PREFIX = "VEH-"; // Static prefix, subclasses might need it for parsing

    private final String id;
    private final String plateNumber;
    private String brand;
    private String model;
    private final int numberOfTires;
    private double dailyFee;

    private boolean isRented;
    private Booking currentRentalPeriod;
    private List<Booking> bookings;

    private boolean isRemoteDeliverable;
    private boolean isRemoteDroppable;

    public Vehicle(String plateNumber, String brand, String model, int numberOfTires, double dailyFee) {
        this.id = ID_PREFIX + nextIdSuffix++;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.numberOfTires = numberOfTires;
        this.dailyFee = dailyFee;

        this.isRented = false;
        this.bookings = new ArrayList<>();
        this.currentRentalPeriod = null;
        this.isRemoteDeliverable = true;
        this.isRemoteDroppable = true;
    }

    public static void updateNextIdSuffix(long highestKnownIdSuffix) {
        nextIdSuffix = Math.max(1L, highestKnownIdSuffix + 1); // Ensure it's at least 1
    }

    public String getId() { return id; }
    public String getPlateNumber() { return plateNumber; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getNumberOfTires() { return numberOfTires; }
    public double getDailyFee() { return dailyFee; }
    public boolean isRented() { return isRented; }
    public List<Booking> getBookings() { return new ArrayList<>(bookings); }
    public boolean isRemoteDeliverable() { return isRemoteDeliverable; }
    public boolean isRemoteDroppable() { return isRemoteDroppable; }
    public Booking getCurrentRentalPeriod() { return currentRentalPeriod; }

    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setDailyFee(double dailyFee) { if (dailyFee > 0) this.dailyFee = dailyFee; }
    protected void setRemoteDeliverable(boolean remoteDeliverable) { this.isRemoteDeliverable = remoteDeliverable; }
    protected void setRemoteDroppable(boolean remoteDroppable) { this.isRemoteDroppable = remoteDroppable; }

    public double getTotalFee(int numberOfDays) {
        if (numberOfDays <= 0) return 0;
        return this.dailyFee * numberOfDays;
    }

    public boolean isAvailable(Date startDate, Date endDate) throws InvalidDateException {
        if (startDate == null || endDate == null || startDate.after(endDate) || startDate.equals(endDate)) {
            throw new InvalidDateException("Invalid date range for availability check.");
        }
        if (isRented && currentRentalPeriod != null && currentRentalPeriod.overlaps(startDate, endDate)) return false;
        for (Booking booking : bookings) {
            if (booking.overlaps(startDate, endDate)) return false;
        }
        return true;
    }

    public void bookMe(Date startDate, Date endDate) throws SorryWeDontHaveThatOneException, InvalidDateException {
        if (!isAvailable(startDate, endDate)) {
            throw new SorryWeDontHaveThatOneException("Vehicle " + id + " (" + plateNumber + ") is not available for booking from " + startDate.toGMTString() + " to " + endDate.toGMTString() + ".");
        }
        this.bookings.add(new Booking(startDate, endDate));
        System.out.println("Vehicle " + id + " ("+ plateNumber + ") successfully booked from " + startDate.toGMTString() + " to " + endDate.toGMTString());
    }

    public void cancelMe(Date startDate, Date endDate) throws NoCancellationYouMustPayException, InvalidDateException {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            throw new InvalidDateException("Invalid dates for booking cancellation.");
        }
        if (isRented && currentRentalPeriod != null &&
            currentRentalPeriod.getStartDate().equals(startDate) &&
            currentRentalPeriod.getEndDate().equals(endDate)) {
            if (!currentRentalPeriod.getStartDate().after(new Date())) {
                throw new NoCancellationYouMustPayException("Cannot cancel booking for " + id + " (" + plateNumber + "): Rental period has started or passed.");
            }
        }
        Booking bookingToRemove = null;
        for (Booking booking : bookings) {
            if (booking.getStartDate().equals(startDate) && booking.getEndDate().equals(endDate)) {
                if (!booking.getStartDate().after(new Date())) {
                     throw new NoCancellationYouMustPayException("Cannot cancel booking for " + id + " (" + plateNumber + "): Booking start date is today or has passed.");
                }
                bookingToRemove = booking;
                break;
            }
        }
        if (bookingToRemove != null) {
            bookings.remove(bookingToRemove);
            System.out.println("Booking for " + id + " (" + plateNumber + ") from " + startDate.toGMTString() + " to " + endDate.toGMTString() + " has been cancelled.");
        } else {
            throw new InvalidDateException("No matching booking found to cancel for vehicle " + id + " (" + plateNumber + ") for the period " + startDate.toGMTString() + " to " + endDate.toGMTString() + ".");
        }
    }

    public void rentMe(Date startDate, Date endDate, String deliveryLocation, String dropOffLocation) throws SorryWeDontHaveThatOneException, InvalidDateException {
        boolean wasBooked = false;
        Booking matchingBooking = null;
        for(Booking b : bookings) {
            if(b.getStartDate().equals(startDate) && b.getEndDate().equals(endDate)) {
                matchingBooking = b;
                wasBooked = true;
                break;
            }
        }
        if (!isAvailable(startDate, endDate) && !wasBooked) {
            throw new SorryWeDontHaveThatOneException("Vehicle " + id + " (" + plateNumber + ") is not available for rental from " + startDate.toGMTString() + " to " + endDate.toGMTString() + ".");
        }
        if (deliveryLocation != null && !deliveryLocation.trim().isEmpty() && !isRemoteDeliverable()) {
            throw new SorryWeDontHaveThatOneException("Vehicle " + id + " (" + plateNumber + ", " + getClass().getSimpleName() + ") cannot be remotely delivered.");
        }
        if (dropOffLocation != null && !dropOffLocation.trim().isEmpty() && !isRemoteDroppable()) {
            throw new SorryWeDontHaveThatOneException("Vehicle " + id + " (" + plateNumber + ", " + getClass().getSimpleName() + ") cannot be remotely dropped off.");
        }
        this.isRented = true;
        this.currentRentalPeriod = new Booking(startDate, endDate);
        if (matchingBooking != null) {
            bookings.remove(matchingBooking);
            System.out.println("Existing booking for " + id + " (" + plateNumber + ") converted to rental.");
        }
        System.out.println("Vehicle " + id + " (" + plateNumber + ") successfully rented from " + startDate.toGMTString() + " to " + endDate.toGMTString() + ".");
        if (isRemoteDeliverable() && deliveryLocation != null && !deliveryLocation.trim().isEmpty()) {
            System.out.println("To be delivered to: " + deliveryLocation);
        }
        if (isRemoteDroppable() && dropOffLocation != null && !dropOffLocation.trim().isEmpty()) {
            System.out.println("To be dropped off at: " + dropOffLocation);
        }
    }

    public double dropMe() throws InvalidDateException {
        if (!isRented || currentRentalPeriod == null) {
            throw new InvalidDateException("Vehicle " + id + " (" + plateNumber + ") cannot be dropped off as it is not currently rented.");
        }
        long rentalDurationMillis = currentRentalPeriod.getEndDate().getTime() - currentRentalPeriod.getStartDate().getTime();
        int numberOfDays = (int) Math.max(1, TimeUnit.MILLISECONDS.toDays(rentalDurationMillis));
        if (numberOfDays == 0 && rentalDurationMillis > 0) numberOfDays = 1;

        double totalFee = getTotalFee(numberOfDays);
        System.out.println("Vehicle " + id + " (" + plateNumber + ") dropped off. Rental period: " +
                           currentRentalPeriod.getStartDate().toGMTString() + " to " + currentRentalPeriod.getEndDate().toGMTString() + " (" + numberOfDays + " days).");
        System.out.println("Total fee: $" + String.format("%.2f", totalFee));
        this.isRented = false;
        this.currentRentalPeriod = null;
        return totalFee;
    }

    public void loadMe(double additionalLoad) throws OverWeightException {
        throw new UnsupportedOperationException("Vehicle type " + this.getClass().getSimpleName() + " ("+id+") does not support loading cargo.");
    }

    public double getLoadingCapacity() {
        return 0;
    }

    @Override
    public String toString() {
        return "Vehicle Type: " + this.getClass().getSimpleName() +
               "\n  ID: " + id +
               "\n  Plate Number: " + plateNumber +
               "\n  Brand: " + brand +
               "\n  Model: " + model +
               "\n  Number of Tires: " + numberOfTires +
               "\n  Daily Fee: $" + String.format("%.2f", dailyFee) +
               "\n  Rented: " + (isRented ? "Yes (Until: " + (currentRentalPeriod != null ? currentRentalPeriod.getEndDate().toGMTString() : "N/A") + ")" : "No") +
               "\n  Bookings: " + (bookings.isEmpty() ? "None" : bookings.size() + " active booking(s)") +
               "\n  Remote Deliverable: " + isRemoteDeliverable +
               "\n  Remote Droppable: " + isRemoteDroppable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return id.equals(vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}