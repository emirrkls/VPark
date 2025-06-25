public class SUV extends Car {
    private String wheelDriveType; // Stores "RWD", "FWD", "4WD", "AWD"

    public SUV(String plateNo, String brand, String model, int numberOfTires, double dailyFee,
               String color, int seatingCapacity, int numOfDoors, String wheelDriveType) {
        super(plateNo, brand, model, numberOfTires, dailyFee, color, seatingCapacity, numOfDoors); // No ID passed
        setWheelDriveTypeInternal(wheelDriveType); // Internal method for validation

        super.setRemoteDeliverable(false);
        super.setRemoteDroppable(false);
    }

    public String getWheelDriveType() { return wheelDriveType; }
    public void setWheelDriveType(String wdType) { setWheelDriveTypeInternal(wdType); }

    private void setWheelDriveTypeInternal(String wdType) {
        if (wdType != null && (wdType.equalsIgnoreCase("RWD") || wdType.equalsIgnoreCase("FWD") ||
                               wdType.equalsIgnoreCase("4WD") || wdType.equalsIgnoreCase("AWD"))) {
            this.wheelDriveType = wdType.toUpperCase(); // Store in a consistent format
        } else {
            System.err.println("Warning: Invalid wheel drive type '" + wdType + "' for SUV " + getId() + ". Setting to null. Valid types are RWD, FWD, 4WD, AWD.");
            this.wheelDriveType = null;
        }
    }

    @Override
    public String toString() {
        return super.toString() +
               "\n  Car Specific Type: SUV" +
               (wheelDriveType != null ? "\n  Wheel Drive: " + wheelDriveType : "\n  Wheel Drive: Not Specified");
    }
}