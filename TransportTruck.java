public class TransportTruck extends Truck {
    private boolean goesAbroad;

    public TransportTruck(String plateNo, String brand, String model, int numberOfTires, double dailyFee,
                          double loadingCapacity, boolean goesAbroad) {
        super(plateNo, brand, model, numberOfTires, dailyFee, loadingCapacity); // No ID passed
        this.goesAbroad = goesAbroad;
    }

    public boolean isGoesAbroad() { 
        return goesAbroad; 
    }
    public void setGoesAbroad(boolean goesAbroad) { this.goesAbroad = goesAbroad; }

    @Override
    public String toString() {
        return super.toString() +
               "\n  Truck Specific Type: Transport Truck" +
               "\n  Goes Abroad: " + goesAbroad;
    }

    @Override
    public double getTotalFee(int numberOfDays) {
        double fee = super.getTotalFee(numberOfDays);
        if (this.goesAbroad) {
            fee += 500.00; // Example fixed additional charge
        }
        return fee;
    }
}