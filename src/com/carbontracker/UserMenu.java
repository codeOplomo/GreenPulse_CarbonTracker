package com.carbontracker;

import java.time.LocalDateTime;
import java.util.Scanner;

public class UserMenu {
    private static final UserManager userManager = new UserManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            displayMenu();
            int choice = getUserChoice();
            running = handleUserChoice(choice);
        }

        scanner.close();
        System.out.println("Application terminated.");
    }

    private static void displayMenu() {
        System.out.println("\nUser Management Menu:");
        System.out.println("1. Create Account");
        System.out.println("2. View Account");
        System.out.println("3. Update Account");
        System.out.println("4. Delete Account");
        System.out.println("5. Add Consumption");
        System.out.println("6. Generate User Report");
        System.out.println("7. Generate All Users Report");
        System.out.println("8. Generate Date Range Report");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        }
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    private static boolean handleUserChoice(int choice) {
        switch (choice) {
            case 1:
                createAccount();
                break;
            case 2:
                viewAccount();
                break;
            case 3:
                updateAccount();
                break;
            case 4:
                deleteAccount();
                break;
            case 5:
                addConsumption();
                break;
            case 6:
                generateUserReport();
                break;
            case 7:
                generateAllUsersReport();
                break;
            case 8:
                generateDateRangeReport();
                break;
            case 9:
                System.out.println("Exiting...");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

    private static void generateUserReport() {
        String id = InputUtils.getValidString("Enter User ID: ");
        userManager.generateUserReport(id);
    }

    private static void generateAllUsersReport() {
        userManager.generateAllUsersReport();
    }

    private static void generateDateRangeReport() {
        LocalDateTime startDate = InputUtils.getValidDateTime("Enter start date (YYYY-MM-DD): ");
        LocalDateTime endDate = InputUtils.getValidDateTime("Enter end date (YYYY-MM-DD): ");
        userManager.generateDateRangeReport(startDate, endDate);
    }

    // Other existing methods remain unchanged
    private static void createAccount() {
        String id = InputUtils.getValidString("Enter ID: ");
        String name = InputUtils.getValidString("Enter Name: ");
        int age = InputUtils.getValidAge();

        if (userManager.getUser(id) != null) {
            System.out.println("User with this ID already exists.");
            return;
        }

        User newUser = new User(id, name, age);
        userManager.addUser(newUser);
        System.out.println("Account created successfully.");
    }

    private static void viewAccount() {
        String id = InputUtils.getValidString("Enter User ID: ");
        User user = userManager.getUser(id);
        if (user != null) {
            user.displayDetails();
        } else {
            System.out.println("User not found.");
        }
    }

    private static void updateAccount() {
        String id = InputUtils.getValidString("Enter User ID: ");
        User user = userManager.getUser(id);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        String name = InputUtils.getValidString("Enter new Name (leave blank to keep unchanged): ");
        if (!name.isEmpty()) {
            user.setName(name);
        }

        int age = InputUtils.getValidAge();
        if (age != -1) {
            user.setAge(age);
        }

        System.out.println("Account updated successfully.");
    }

    private static void deleteAccount() {
        String id = InputUtils.getValidString("Enter User ID: ");
        if (userManager.getUser(id) != null) {
            userManager.removeUser(id);
            System.out.println("Account deleted successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    private static void addConsumption() {
        String id = InputUtils.getValidString("Enter User ID: ");
        User user = userManager.getUser(id);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        double carbonAmount = InputUtils.getValidDouble("Enter carbon amount: ");
        LocalDateTime startDate = InputUtils.getValidDateTime("Enter start date (YYYY-MM-DD): ");
        LocalDateTime endDate = InputUtils.getValidDateTime("Enter end date (YYYY-MM-DD): ");

        // Call addEntry with userId
        user.getConsumption().addEntry(id, carbonAmount, startDate, endDate);
        System.out.println("Consumption added successfully.");
    }

}
