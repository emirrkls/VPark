# VPark - Vehicle Hiring System

VPark is a comprehensive console-based Vehicle Hiring System designed to manage vehicle rentals, bookings, customer information, and administrative tasks for a fictional company, VPark. The system supports different types of vehicles, each with specific attributes and rental rules, and provides distinct interfaces for administrative and customer roles.

## Table of Contents

- [Project Description](#project-description)
- [Core Features](#core-features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Setup and Running](#setup-and-running)
- [How to Use](#how-to-use)
  - [Role Selection](#role-selection)
  - [Admin Menu](#admin-menu)
  - [Customer Menu](#customer-menu)
- [Data Persistence](#data-persistence)
- [Exception Handling](#exception-handling)
- [Future Enhancements](#future-enhancements)
- [Screenshots](#screenshots)

## Project Description

The VPark system allows for the hiring of various vehicles, including cars (Sports Cars, SUVs, Station Wagons) and trucks (Small Trucks, Transport Trucks), for different time periods. Each vehicle type has unique properties, fees, and rules, such as booking restrictions, remote delivery/drop-off capabilities, and load capacities.

The system is designed with Object-Oriented Programming principles, featuring a clear class hierarchy, encapsulation, polymorphism, and custom exception handling. It supports operations like booking vehicles for future rentals, renting vehicles, returning vehicles, and managing additional loads for capable vehicles. User interactions are managed through a text-based user interface (TUI) with separate menus for administrators and customers. The system state, including vehicle inventory and customer data, is persisted to a binary file.

## Core Features

*   **Vehicle Management:**
    *   Add new vehicles (Cars, Trucks with specific subtypes) to the inventory.
    *   Remove vehicles from the inventory.
    *   Display a list of all vehicles with detailed information.
    *   Display vehicles available for rent within a specified date range.
    *   Overloaded display of available vehicles filtered by type (e.g., "Car", "SUV", "Truck").
*   **Customer Management:**
    *   Implicit customer registration (new customers added when interacting).
    *   Admins can display a list of all registered customers.
    *   Customers are identified by a unique ID (e.g., `CUST-1`).
*   **Rental & Booking Operations:**
    *   **Booking:** Customers can book vehicles for future dates.
        *   Trucks require booking at least 7 days in advance.
    *   **Booking Cancellation:** Customers can cancel their bookings.
        *   Cancellation is not allowed if the booking/rental period has started or passed.
    *   **Renting:** Customers can rent available vehicles.
        *   Supports remote delivery and drop-off options for eligible vehicles.
    *   **Dropping Off:** Customers can return rented vehicles, and the total fee is calculated.
    *   **Loading:** Customers can add loads to vehicles with cargo capacity (Station Wagons, Trucks).
        *   Prevents overloading beyond the vehicle's capacity.
*   **User Roles & Interface:**
    *   **Admin Role:** Access to vehicle inventory management, customer listing, and report generation.
    *   **Customer Role:** Access to view vehicles, book, rent, cancel, drop-off, and load vehicles.
    *   Interactive Text-Based User Interface (TUI) for system operations.
*   **Data Persistence:**
    *   The system's current state (vehicles, customers, bookings) is automatically saved to a binary file (`vpark_data.dat`) upon exiting.
    *   Data is loaded from the file upon startup.
    *   If no data file is found, the system starts with a fresh state and can populate initial sample data.
*   **Reporting:**
    *   Admins can generate a daily report summarizing vehicle status (all, rented, booked), and registered customer information into a text file.
*   **Detailed Vehicle Hierarchy:**
    *   `Vehicle` (Abstract)
        *   `Car` (Abstract)
            *   `SportsCar` (Bookable, HP attribute, specific fee calculation)
            *   `StationWagon` (Loading capacity)
            *   `SUV` (Wheel drive type, neither remote deliverable nor droppable)
        *   `Truck` (Abstract, loading capacity, 7-day advance booking, not remote deliverable/droppable)
            *   `SmallTruck`
            *   `TransportTruck` (Goes abroad attribute, specific fee calculation)
*   **Custom Exception Handling:** For operational errors like unavailable vehicles, invalid dates, overweight loads, and unauthorized cancellations.

## Technologies Used

*   **Java:** Latest version (as of project development)
*   **IDE:** Apache NetBeans
*   **Object-Oriented Programming (OOP):** Core principles including Inheritance, Polymorphism, Encapsulation, and Abstraction are extensively used.
*   **Java `java.io.Serializable`:** For object serialization and data persistence.
*   **Java `java.util.Date` & `java.text.SimpleDateFormat`:** For handling dates and times.
*   **Java Collections Framework:** `ArrayList` for managing lists of vehicles, customers, and bookings.

## Project Structure

The project is organized into several classes, each with a specific responsibility:

*   **`Vehicle.java`**: Abstract base class for all vehicles. Manages common attributes (ID, plate number, brand, model, daily fee, rental status, bookings) and core operations (availability check, booking, renting, dropping, loading).
*   **`Car.java`**: Abstract subclass of `Vehicle`, representing cars. Adds car-specific attributes like color, seating capacity, and number of doors.
    *   **`SportsCar.java`**: Concrete `Car` type with horsepower and special fee calculation.
    *   **`SUV.java`**: Concrete `Car` type with wheel drive information; not remote deliverable/droppable.
    *   **`StationWagon.java`**: Concrete `Car` type with a small loading capacity.
*   **`Truck.java`**: Abstract subclass of `Vehicle`, representing trucks. Adds loading capacity and enforces a 7-day advance booking rule; not remote deliverable/droppable.
    *   **`SmallTruck.java`**: Concrete `Truck` type.
    *   **`TransportTruck.java`**: Concrete `Truck` type, with a flag indicating if it goes abroad.
*   **`Customer.java`**: Represents a customer with an auto-generated ID, name, and contact information.
*   **`Booking.java`**: Represents a booking or rental period with start and end dates. Includes logic to check for overlapping date ranges.
*   **`VehiclePark.java`**: The central management class. It holds lists of all vehicles and registered customers and orchestrates operations like adding/removing vehicles, finding vehicles/customers, and delegating actions (book, rent, etc.) to the respective `Vehicle` objects.
*   **`Test.java`**: The main class containing the `main` method. It handles the Text User Interface (TUI), user input, menu navigation for both admin and customer roles, and initializes data loading/saving.
*   **Exception Classes:**
    *   `InvalidDateException.java`
    *   `NoCancellationYouMustPayException.java`
    *   `OverWeightException.java`
    *   `SorryWeDontHaveThatOneException.java`

## Setup and Running

1.  **Prerequisites:**
    *   Java Development Kit (JDK) - Latest version recommended.
    *   Apache NetBeans IDE (Version 20 or compatible) is recommended for ease of use.
2.  **Getting the Code:**
    *   Clone the repository or download the source code files.
3.  **IDE Setup (Apache NetBeans):**
    *   Open Apache NetBeans IDE.
    *   Select `File > Open Project...`
    *   Navigate to the directory where you saved the project files and select it.
    *   The IDE should recognize the project structure.
4.  **Running the Application:**
    *   Locate the `Test.java` file in the project explorer.
    *   Right-click on `Test.java` and select `Run File` (or press `Shift + F6`).
    *   The application will start in the IDE's output console.

## How to Use

Upon running the application, you will be presented with a main menu to select your role.

### Role Selection


Welcome to VPark Vehicle Hiring System
Select your role:

Admin

Customer

Exit System
Enter your choice:

Generated code
Enter `1` for Admin, `2` for Customer, or `0` to save data and exit.

### Admin Menu

If you select 'Admin', you will see the following options:

--- Admin Menu ---

Display All Vehicles

Display Available Vehicles (by date)

Add New Vehicle

Remove Vehicle

Generate Daily Report

Display All Customers

Back to Main Menu
Enter your choice:

Generated code
*   **1. Display All Vehicles:** Lists all vehicles in the system with their details and current status.
*   **2. Display Available Vehicles:** Prompts for a start and end date, then lists vehicles available during that period.
*   **3. Add New Vehicle:** Guides through adding a new Car or Truck with its specific subtype and attributes.
*   **4. Remove Vehicle:** Prompts for a Vehicle ID to remove it from the system (if not currently rented).
*   **5. Generate Daily Report:** Prompts for a filename and creates a text report of system status.
*   **6. Display All Customers:** Lists all registered customers.
*   **0. Back to Main Menu:** Returns to the role selection screen.

### Customer Menu

If you select 'Customer', you will be asked to enter your Customer ID (e.g., `CUST-1`). If valid, you will see:

--- Customer Menu (Customer Name) ---

Display All Vehicles

Display Available Vehicles (by date)

Display Available Vehicles (by date and type)

Book a Vehicle

Cancel My Booking

Rent a Vehicle

Drop a Vehicle

Load a Vehicle

Back to Main Menu (Logout)
Enter your choice:

Generated code
*   **1. Display All Vehicles:** Lists all vehicles.
*   **2. Display Available Vehicles (by date):** Prompts for dates and lists available vehicles.
*   **3. Display Available Vehicles (by date and type):** Prompts for dates and a vehicle type (e.g., "SUV", "Car", "Truck") and lists matching available vehicles.
*   **4. Book a Vehicle:** Prompts for Vehicle ID and booking dates.
*   **5. Cancel My Booking:** Prompts for Vehicle ID and booking dates to cancel.
*   **6. Rent a Vehicle:** Prompts for Vehicle ID, rental dates, and optionally delivery/drop-off locations if the vehicle supports them.
*   **7. Drop a Vehicle:** Prompts for the Vehicle ID of a currently rented vehicle to return it.
*   **8. Load a Vehicle:** Prompts for Vehicle ID and amount to load onto a compatible vehicle.
*   **0. Back to Main Menu (Logout):** Returns to the role selection screen.

Date inputs should be in `dd/MM/yyyy` format.

## Data Persistence

The application state (all vehicles, customers, and their current bookings/rental status) is saved into a binary file named `vpark_data.dat` in the project's root directory.
*   **Loading:** Data is automatically loaded when the application starts. If `vpark_data.dat` is not found or is corrupted, the system initializes with an empty state, and sample data is populated to demonstrate functionality.
*   **Saving:** Data is automatically saved when the user chooses to exit the system from the main menu (option `0`).
*   The system correctly handles static ID counters for `Vehicle` and `Customer` classes during serialization and deserialization to ensure ID uniqueness across sessions.

## Exception Handling

The system implements custom exceptions to manage various error scenarios gracefully:
*   `InvalidDateException`: For invalid date inputs or logical errors with date ranges.
*   `NoCancellationYouMustPayException`: When attempting to cancel a booking that is no longer eligible for cancellation (e.g., rental period started).
*   `OverWeightException`: When attempting to load a vehicle beyond its cargo capacity.
*   `SorryWeDontHaveThatOneException`: When a requested vehicle is unavailable for booking/renting, or an operation cannot be performed (e.g., remote delivery for a non-deliverable vehicle).

Error messages are displayed to the user, allowing them to understand the issue and try again.

## Future Enhancements

*   **Graphical User Interface (GUI):** Develop a more user-friendly GUI using Java Swing or JavaFX.
*   **Advanced Reporting:** Implement more detailed and customizable reports.
*   **Database Integration:** Replace binary file persistence with a relational or NoSQL database for better scalability and data management.
*   **User Authentication:** Implement a more robust authentication system for users.
*   **Enhanced Search/Filter:** More complex search and filtering options for vehicles.

## Screenshots

*(Placeholder for screenshots )*

---
