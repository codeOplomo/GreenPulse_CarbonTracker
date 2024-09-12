package com.carbontracker;

import com.carbontracker.config.DbConnection;
import com.carbontracker.services.AccountManager;
//import com.carbontracker.services.ReportGenerator;
import com.carbontracker.utils.UserInputHandler;

import java.sql.Connection;

public class Menu {
    public static void main(String[] args) {
        Connection connection = DbConnection.getInstance().getConnection();

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
                AccountManager.createAccount();
                break;
            case 2:
                AccountManager.viewAccount();
                break;
            case 3:
                AccountManager.updateAccount();
                break;
            case 4:
                AccountManager.deleteAccount();
                break;
            case 5:
                AccountManager.addConsumption();
                break;
            case 6:
                //ReportGenerator.userReport(); // ReportGenerator will use singleton instances internally
                break;
            case 7:
                //ReportGenerator.allUsersReport(); // ReportGenerator will use singleton instances internally
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
