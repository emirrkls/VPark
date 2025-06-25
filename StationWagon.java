public class StationWagon extends Car {
    private double loadingCapacitySW;

    public StationWagon(String plateNo, String brand, String model, int numberOfTires, double dailyFee,
                        String color, int seatingCapacity, int numOfDoors, double loadingCapacitySW) {
        super(plateNo, brand, model, numberOfTires, dailyFee, color, seatingCapacity, numOfDoors); // No ID passed
        this.loadingCapacitySW = loadingCapacitySW;
    }

    public void setLoadingCapacitySW(double loadingCapacitySW) { this.loadingCapacitySW = loadingCapacitySW; }

    @Override
    public String toString() {
        return super.toString() +
               "\n  Car Specific Type: Station Wagon" +
               "\n  Loading Capacity: " + loadingCapacitySW + " units";
    }

    @Override
    public void loadMe(double additionalLoad) throws OverWeightException {
        if (this.loadingCapacitySW <= 0) {
            throw new UnsupportedOperationException("This Station Wagon (" + getId() + ") does not have a defined loading capacity.");
        }
        if (additionalLoad > this.loadingCapacitySW) {
             throw new OverWeightException("Load of " + additionalLoad + " units exceeds Station Wagon " + getId() +
                                          "'s capacity of " + this.loadingCapacitySW + " units.");
        }
        System.out.println(additionalLoad + " units loaded to Station Wagon " + getId() + " (" + getPlateNumber() + ").");
    }

    @Override
    public double getLoadingCapacity() {
        return this.loadingCapacitySW;
    }
}