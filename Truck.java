import java.util.Date; // For bookMe method signature
import java.util.concurrent.TimeUnit; // For bookMe date diff

public abstract class Truck extends Vehicle {
    private double loadingCapacity;

    public Truck(String plateNo, String brand, String model, int numberOfTires, double dailyFee,
                 double loadingCapacity) {
        super(plateNo, brand, model, numberOfTires, dailyFee); // No ID passed
        this.loadingCapacity = loadingCapacity;
        super.setRemoteDeliverable(false);
        super.setRemoteDroppable(false);
    }

    @Override
    public double getLoadingCapacity() { return this.loadingCapacity; }
    public void setLoadingCapacity(double loadingCapacity) { this.loadingCapacity = loadingCapacity; }

    @Override
    public void bookMe(Date startDate, Date endDate) throws SorryWeDontHaveThatOneException, InvalidDateException {
        Date today = new Date();
        long diffInMillies = startDate.getTime() - today.getTime();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillies);

        if (startDate.before(today) || diffInDays < 7) {
            throw new InvalidDateException("Booking failed for truck " + getId() + " ("+getPlateNumber()+")" +
                                           ": Trucks must be booked at least 7 days in advance. Requested: " +
                                           (startDate.before(today) ? "in the past." : diffInDays + " day(s) in advance."));
        }
        super.bookMe(startDate, endDate);
    }

    @Override
    public void loadMe(double additionalLoad) throws OverWeightException {
        if (this.loadingCapacity <= 0) {
            throw new UnsupportedOperationException("This Truck (" + getId() + ") does not have a defined loading capacity.");
        }
        if (additionalLoad > this.loadingCapacity) {
             throw new OverWeightException("Load of " + additionalLoad + " tons exceeds Truck " + getId() +
                                          "'s ("+getPlateNumber()+") total capacity of " + this.loadingCapacity + " tons.");
        }
        System.out.println(additionalLoad + " tons loaded to Truck " + getId() + " (" + getPlateNumber() + ").");
    }

    @Override
    public String toString() {
        return super.toString() +
               "\n  Loading Capacity: " + String.format("%.2f", this.loadingCapacity) + " tons";
    }
}