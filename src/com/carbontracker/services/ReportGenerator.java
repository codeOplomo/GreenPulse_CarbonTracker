package com.carbontracker.services;

import com.carbontracker.models.Consumption;
import com.carbontracker.models.User;
import com.carbontracker.utils.UserInputHandler;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
                LocalDate startWeek = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
                LocalDate endWeek = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");
                displayWeeklyConsumption(user.getConsumption(), startWeek, endWeek);
                break;
            case 3:
                LocalDate startMonth = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
                LocalDate endMonth = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");
                displayMonthlyConsumption(user.getConsumption(), startMonth, endMonth);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    public static void generateAllUsersReport() {
        LocalDate startDate = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
        LocalDate endDate = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");
        int reportType = getReportType();

        // Collect all users who have consumption data in the date range
        Set<User> activeUsers = userManager.getAllUsers().stream()
                .filter(user -> hasConsumptionInDateRange(user, startDate, endDate))
                .collect(Collectors.toSet());

        if (activeUsers.isEmpty()) {
            System.out.println("No users with consumption data in the specified date range.");
            return;
        }

        System.out.println("Total users with consumption data between " + startDate + " and " + endDate + ": " + activeUsers.size());

        // Aggregate consumption data for all active users
        Map<LocalDate, Double> aggregatedMonthlyConsumption = new HashMap<>();

        for (User user : activeUsers) {
            Consumption consumption = user.getConsumption();
            LocalDate currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());

            while (!currentStart.isAfter(endDate)) {
                double monthlyConsumption = consumption.getTotalCarbonForMonth(currentStart);
                aggregatedMonthlyConsumption.merge(currentStart, monthlyConsumption, Double::sum);

                // Move to the next month
                currentStart = currentStart.plusMonths(1);
                currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
            }
        }

        // Print aggregated monthly consumption
        System.out.println("Aggregated Monthly Consumption Report:");
        LocalDate currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());

        while (!currentStart.isAfter(endDate)) {
            double totalConsumption = aggregatedMonthlyConsumption.getOrDefault(currentStart, 0.0);
            System.out.println("Month from " + currentStart + " to " + currentEnd + ": " + totalConsumption);

            // Move to the next month
            currentStart = currentStart.plusMonths(1);
            currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
        }
    }

    private static boolean hasConsumptionInDateRange(User user, LocalDate startDate, LocalDate endDate) {
        Consumption consumption = user.getConsumption();
        LocalDate currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());

        while (!currentStart.isAfter(endDate)) {
            double monthlyConsumption = consumption.getTotalCarbonForMonth(currentStart);
            if (monthlyConsumption > 0) {
                return true; // User has consumption data in the range
            }

            // Move to the next month
            currentStart = currentStart.plusMonths(1);
            currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
        }

        return false; // No consumption data in the range
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

    private static void displayWeeklyConsumption(Consumption consumption, LocalDate startDate, LocalDate endDate) {
        LocalDate currentStart = startDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate currentEnd = currentStart.plusWeeks(1).minusDays(1);

        // Adjust the endDate to be inclusive of the week ending on the next Sunday
        endDate = endDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        while (!currentStart.isAfter(endDate)) {
            double weeklyConsumption = consumption.getTotalCarbonForWeek(currentStart);

            // Print start and end of the week
            System.out.println("Week from " + currentStart + " to " + currentEnd + ": " + weeklyConsumption);

            // Move to the next week
            currentStart = currentStart.plusWeeks(1);
            currentEnd = currentEnd.plusWeeks(1);
        }
    }

    private static void displayMonthlyConsumption(Consumption consumption, LocalDate startDate, LocalDate endDate) {
        // Start from the first month of the startDate
        LocalDate currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());

        // Adjust endDate to the last day of the month if it's not already
        endDate = endDate.with(TemporalAdjusters.lastDayOfMonth());

        // Ensure we cover the entire range
        if (currentEnd.isBefore(startDate)) {
            currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
            currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
        }

        while (!currentStart.isAfter(endDate)) {
            double monthlyConsumption = consumption.getTotalCarbonForMonth(currentStart);

            // Print start and end of the month
            System.out.println("Month from " + currentStart + " to " + currentEnd + ": " + monthlyConsumption);

            // Move to the next month
            currentStart = currentStart.plusMonths(1);
            currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
        }
    }
}
