package com.carbontracker;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static int getValidAge() {
        while (true) {
            System.out.print("Enter Age: ");
            if (scanner.hasNextInt()) {
                int age = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                if (age >= 0) {
                    return age;
                } else {
                    System.out.println("Age cannot be negative.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid age.");
                scanner.next(); // Consume the invalid input
            }
        }
    }

    public static double getValidDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                scanner.nextLine(); // Consume the newline character
                return value;
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // Consume the invalid input
            }
        }
    }

    public static String getValidString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static LocalDateTime getValidDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                // Read date part only
                LocalDate date = LocalDate.parse(scanner.nextLine(), dateFormatter);
                // Convert LocalDate to LocalDateTime with default time (00:00)
                return date.atStartOfDay();
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format.");
            }
        }
    }
}
