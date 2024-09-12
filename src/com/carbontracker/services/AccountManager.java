package com.carbontracker.services;

import com.carbontracker.models.*;
import com.carbontracker.models.enumeration.ConsumptionType;
import com.carbontracker.utils.UserInputHandler;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class AccountManager {
    private static final UserService userService = UserService.getInstance();

    public static void viewAccount() {
        UUID id = getUserId();
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isPresent()) {
            optionalUser.get().displayDetails();
        } else {
            System.out.println("User not found.");
        }
    }

    public static void createAccount() {
        // Generate a new UUID for the user
        UUID id = UUID.randomUUID();
        String name = UserInputHandler.getValidString("Enter Name: ");
        int age = UserInputHandler.getValidAge();

        User newUser = new User(id, name, age); // Pass UUID directly
        Optional<User> addedUser = userService.add(newUser);
        if (addedUser.isPresent()) {
            System.out.println("Account created successfully.");
        } else {
            System.out.println("Failed to create account.");
        }
    }

    public static void updateAccount() {
        UUID id = getUserId();
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            System.out.println("User not found.");
            return;
        }

        String name = UserInputHandler.getValidString("Enter new Name (leave blank to keep unchanged): ");
        int age = UserInputHandler.getValidAge();

        // Update the user details
        Optional<User> updatedUser = userService.update(id, !name.isEmpty() ? name : optionalUser.get().getName(), age);
        if (updatedUser.isPresent()) {
            System.out.println("Account updated successfully.");
        } else {
            System.out.println("Failed to update account.");
        }
    }

    public static void deleteAccount() {
        UUID id = getUserId();
        if (userService.findById(id).isPresent()) {
            boolean deleted = userService.delete(id);
            if (deleted) {
                System.out.println("Account deleted successfully.");
            } else {
                System.out.println("Failed to delete account.");
            }
        } else {
            System.out.println("User not found.");
        }
    }

    public static void addConsumption() {
        UUID userId = getUserId(); // Get user ID as UUID
        Optional<User> optionalUser = userService.findById(userId);

        if (optionalUser.isEmpty()) {
            System.out.println("User not found.");
            return;
        }

        User user = optionalUser.get();

        // Get start and end dates from user input
        LocalDate startDate = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
        LocalDate endDate = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");

        // Get consumption type
        ConsumptionType type = UserInputHandler.getValidEnum("Enter consumption type (TRANSPORT, HOUSING, FOOD): ", ConsumptionType.class);

        // Get amount and create the appropriate Consumption instance based on the type
        double amount = UserInputHandler.getValidDouble("Enter the carbon amount: ");
        Consumption tempConsumption = null;

        switch (type) {
            case TRANSPORT:
                double distance = UserInputHandler.getValidDouble("Enter distance travelled (in km): ");
                String vehicleType = UserInputHandler.getValidString("Enter type of vehicle: ");
                tempConsumption = new Transport(UUID.randomUUID(), vehicleType, distance, amount, startDate, endDate, userId);
                break;
            case HOUSING:
                double energyConsumption = UserInputHandler.getValidDouble("Enter energy consumption (in kWh): ");
                String energyType = UserInputHandler.getValidString("Enter type of energy: ");
                tempConsumption = new Housing(UUID.randomUUID(), energyType, energyConsumption, amount, startDate, endDate, userId);
                break;
            case FOOD:
                String alimentType = UserInputHandler.getValidString("Enter type of food: ");
                double weight = UserInputHandler.getValidDouble("Enter weight of food (in kg): ");
                tempConsumption = new Food(UUID.randomUUID(), alimentType, weight, amount, startDate, endDate, userId);
                break;
            default:
                System.out.println("Invalid consumption type.");
                return;
        }

        // Add the consumption to the user's record
        boolean success = ConsumptionService.getInstance().addEntry(user, tempConsumption, startDate, endDate);

        if (success) {
            System.out.println("Consumption added successfully.");
        } else {
            System.out.println("Failed to add consumption.");
        }
    }



    private static UUID getUserId() {
        String idString = UserInputHandler.getValidString("Enter User ID: ");
        try {
            return UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid User ID format.");
            throw e; // or handle it as needed
        }
    }


    /* public static void addConsumption() {
        String userId = UserInputHandler.getValidString("Enter User ID: ");
        User user = userService.findById(userId);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        // Prompt for consumption type
        ConsumptionType type = UserInputHandler.getValidEnum("Enter consumption type (TRANSPORT, LOGEMENT, ALIMENTATION): ", ConsumptionType.class);

        // Prompt for amount and create the appropriate Consumption instance based on the type
        double amount = UserInputHandler.getValidDouble("Enter the carbon amount: ");
        Consumption tempConsumption = null;
        double calculatedImpact = 0;

        switch (type) {
            case TRANSPORT:
                double distance = UserInputHandler.getValidDouble("Enter distance travelled (in km): ");
                String vehicleType = UserInputHandler.getValidString("Enter type of vehicle: ");
                tempConsumption = new Transport(distance, vehicleType, amount);
                calculatedImpact = tempConsumption.calculerImpact();
                break;
            case LOGEMENT:
                double energyConsumption = UserInputHandler.getValidDouble("Enter energy consumption (in kWh): ");
                String energyType = UserInputHandler.getValidString("Enter type of energy: ");
                tempConsumption = new Logement(energyConsumption, energyType, amount);
                calculatedImpact = tempConsumption.calculerImpact();
                break;
            case ALIMENTATION:
                String alimentType = UserInputHandler.getValidString("Enter type of food: ");
                double weight = UserInputHandler.getValidDouble("Enter weight of food (in kg): ");
                tempConsumption = new Alimentation(alimentType, weight, amount);
                calculatedImpact = tempConsumption.calculerImpact();
                break;
            default:
                System.out.println("Invalid consumption type.");
                return;
        }

        // Add the calculated impact to the user's daily consumptions
        LocalDate startDate = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
        LocalDate endDate = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");
        ConsumptionService.getInstance().addEntry(user, type, amount, startDate, endDate);
        //consumptionService.addEntry(user.getConsumptions(), totalCarbonAmount, startDate, endDate);

        System.out.println("Consumption added successfully.");
    }*/

}
