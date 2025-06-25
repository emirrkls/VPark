public abstract class Car extends Vehicle {
    private String color;
    private int seatingCapacity;
    private int numOfDoors;

    public Car(String plateNo, String brand, String model, int numberOfTires, double dailyFee,
               String color, int seatingCapacity, int numOfDoors) {
        super(plateNo, brand, model, numberOfTires, dailyFee); // No ID passed to super
        this.color = color;
        this.seatingCapacity = seatingCapacity;
        this.numOfDoors = numOfDoors;
    }

    public String getColor() { return color; }
    public int getSeatingCapacity() { return seatingCapacity; }
    public int getNumOfDoors() { return numOfDoors; }

    public void setColor(String color) { this.color = color; }
    public void setSeatingCapacity(int seatingCapacity) { this.seatingCapacity = seatingCapacity; }
    public void setNumOfDoors(int numOfDoors) { this.numOfDoors = numOfDoors; }

    @Override
    public String toString() {
        return super.toString() +
               "\n  Color: " + color +
               "\n  Seating Capacity: " + seatingCapacity +
               "\n  Number of Doors: " + numOfDoors;
    }
}