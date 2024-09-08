package com.carbontracker.services;

import com.carbontracker.models.User;
import com.carbontracker.utils.UserInputHandler;

import java.time.LocalDate;

public class AccountManager {
    private static final UserService userService = UserService.getInstance();
    private static final ConsumptionService consumptionService = ConsumptionService.getInstance(); // Use singleton instance

    public static void createAccount() {
        String id = UserInputHandler.getValidString("Enter ID: ");
        String name = UserInputHandler.getValidString("Enter Name: ");
        int age = UserInputHandler.getValidAge();

        if (userService.getUser(id) != null) {
            System.out.println("User with this ID already exists.");
            return;
        }

        User newUser = new User(id, name, age);
        userService.addUser(newUser);
        System.out.println("Account created successfully.");
    }

    public static void viewAccount() {
        String id = UserInputHandler.getValidString("Enter User ID: ");
        User user = userService.getUser(id);
        if (user != null) {
            user.displayDetails();
        } else {
            System.out.println("User not found.");
        }
    }

    public static void updateAccount() {
        String id = UserInputHandler.getValidString("Enter User ID: ");
        User user = userService.getUser(id);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        String name = UserInputHandler.getValidString("Enter new Name (leave blank to keep unchanged): ");
        if (!name.isEmpty()) {
            user.setName(name);
        }

        int age = UserInputHandler.getValidAge();
        if (age > 0) {
            user.setAge(age);
        }

        System.out.println("Account updated successfully.");
    }

    public static void deleteAccount() {
        String id = UserInputHandler.getValidString("Enter User ID: ");
        if (userService.getUser(id) != null) {
            userService.removeUser(id);
            System.out.println("Account deleted successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    public static void addConsumption() {
        String userId = UserInputHandler.getValidString("Enter User ID: ");
        User user = userService.getUser(userId);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        double totalCarbonAmount = UserInputHandler.getValidDouble("Enter total carbon amount: ");
        LocalDate startDate = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
        LocalDate endDate = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");

        consumptionService.addEntry(user.getConsumption(), totalCarbonAmount, startDate, endDate);
        System.out.println("Consumption added successfully.");
    }
}
