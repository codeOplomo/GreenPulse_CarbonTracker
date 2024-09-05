package com.carbontracker;

import com.carbontracker.services.ReportGenerator;
import com.carbontracker.services.UserAccountManager;
import com.carbontracker.utils.UserInputHandler;

public class Menu {
    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            displayMenu();
            int choice = UserInputHandler.getUserChoice();
            running = handleUserChoice(choice);
        }

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
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    private static boolean handleUserChoice(int choice) {
        switch (choice) {
            case 1:
                UserAccountManager.createAccount();
                break;
            case 2:
                UserAccountManager.viewAccount();
                break;
            case 3:
                UserAccountManager.updateAccount();
                break;
            case 4:
                UserAccountManager.deleteAccount();
                break;
            case 5:
                UserAccountManager.addConsumption();
                break;
            case 6:
                ReportGenerator.generateUserReport();
                break;
            case 7:
                ReportGenerator.generateAllUsersReport();
                break;
            case 8:
                System.out.println("Exiting...");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        return true;
    }

}
