package com.carbontracker.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class UserInputHandler {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // clear invalid input
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        return choice;
    }

    public static String getValidString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static LocalDate getValidDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format.");
            }
        }
    }

    public static double getValidDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = scanner.nextDouble();
                scanner.nextLine(); // consume the newline character
                return value;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // clear the invalid input
            }
        }
    }

    public static int getValidAge() {
        while (true) {
            try {
                System.out.print("Enter Age: ");
                int age = scanner.nextInt();
                scanner.nextLine(); // consume the newline character
                if (age > 0) return age;
                else System.out.println("Age must be positive.");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // clear the invalid input
            }
        }
    }
}
