public class SportsCar extends Car {
    private int horsePower;

    public SportsCar(String plateNo, String brand, String model, int numberOfTires, double dailyFee,
                     String color, int seatingCapacity, int numOfDoors, int horsePower) {
        super(plateNo, brand, model, numberOfTires, dailyFee, color, seatingCapacity, numOfDoors); // No ID passed
        this.horsePower = horsePower;
    }

    public int getHorsePower() { return horsePower; }
    public void setHorsePower(int horsePower) { this.horsePower = horsePower; }

    @Override
    public String toString() {
        return super.toString() +
               "\n  Car Specific Type: Sports Car" +
               "\n  Horse Power: " + horsePower + " HP";
    }

    @Override
    public double getTotalFee(int numberOfDays) {
        double fee = super.getTotalFee(numberOfDays);
        if (this.horsePower > 0) {
            fee += (this.horsePower * 0.1) * numberOfDays;
        }
        return fee;
    }
}