public class SmallTruck extends Truck {
    public SmallTruck(String plateNo, String brand, String model, int numberOfTires, double dailyFee,
                      double loadingCapacity) {
        super(plateNo, brand, model, numberOfTires, dailyFee, loadingCapacity); // No ID passed
    }

    @Override
    public String toString() {
        return super.toString() +
               "\n  Truck Specific Type: Small Truck";
    }
}