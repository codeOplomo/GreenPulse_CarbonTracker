package com.carbontracker.services;

import com.carbontracker.models.Consumption;
import com.carbontracker.models.User;
import com.carbontracker.utils.UserInputHandler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {
    private static final UserManager userManager = UserManager.getInstance();

    public static void generateUserReport() {
        String id = UserInputHandler.getValidString("Enter User ID: ");
        User user = userManager.getUser(id);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        int reportType = getReportType();
        LocalDate now = LocalDate.now();
        switch (reportType) {
            case 1:
                displayDailyConsumption(user.getConsumption(),
                        UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): "),
                        UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): "));
                break;
            case 2:
                System.out.println("Weekly report for user " + id + ": " + user.getConsumption().getTotalCarbonForWeek(now));
                break;
            case 3:
                System.out.println("Monthly report for user " + id + ": " + user.getConsumption().getTotalCarbonForMonth(now));
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static int getReportType() {
        System.out.println("\nReport Type Menu:");
        System.out.println("1. Daily Report");
        System.out.println("2. Weekly Report");
        System.out.println("3. Monthly Report");
        System.out.print("Enter your choice: ");
        return UserInputHandler.getUserChoice();
    }


    private static void displayDailyConsumption(Consumption consumption, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> dailyConsumptions = consumption.calculateDailyConsumptionAverages();

        if (dailyConsumptions.isEmpty()) {
            System.out.println("No consumption data available.");
            return;
        }

        // Filter and display consumption for the specified date range
        Map<LocalDate, Double> filteredConsumption = dailyConsumptions.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (filteredConsumption.isEmpty()) {
            System.out.println("No consumption data available for the specified date range.");
            return;
        }

        System.out.println("Daily Consumption Report:");
        filteredConsumption.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }


    public static void generateAllUsersReport() {
        // Placeholder for all users report
    }

    public static void generateDateRangeReport() {
        // Placeholder for date range report
    }
}
