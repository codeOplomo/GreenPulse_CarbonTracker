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

        // Choose the report generation logic based on the selected report type
        switch (reportType) {
            case 1:
                generateDailyReport(activeUsers, startDate, endDate);
                break;
            case 2:
                generateWeeklyReport(activeUsers, startDate, endDate);
                break;
            case 3:
                generateMonthlyReport(activeUsers, startDate, endDate);
                break;
            default:
                System.out.println("Invalid report type.");
        }
    }

    private static void generateDailyReport(Set<User> activeUsers, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> aggregatedDailyConsumption = new HashMap<>();

        for (User user : activeUsers) {
            Consumption consumption = user.getConsumption();
            Map<LocalDate, Double> dailyConsumptions = consumption.calculateDailyConsumptionAverages();

            // Filter daily consumptions within the specified range and aggregate
            dailyConsumptions.forEach((date, amount) -> {
                if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                    aggregatedDailyConsumption.merge(date, amount, Double::sum);
                }
            });
        }

        // Print aggregated daily consumption
        System.out.println("Aggregated Daily Consumption Report:");
        aggregatedDailyConsumption.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    private static void generateWeeklyReport(Set<User> activeUsers, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> aggregatedWeeklyConsumption = new HashMap<>();
        LocalDate currentStart = startDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate currentEnd = currentStart.plusWeeks(1).minusDays(1);

        // Adjust the endDate to be inclusive of the week ending on the next Sunday
        endDate = endDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        while (!currentStart.isAfter(endDate)) {
            final LocalDate weekStart = currentStart;
            final LocalDate weekEnd = currentEnd;

            double weeklyConsumption = activeUsers.stream()
                    .mapToDouble(user -> user.getConsumption().getTotalCarbonForWeek(weekStart))
                    .sum();

            // Print start and end of the week
            System.out.println("Week from " + weekStart + " to " + weekEnd + ": " + weeklyConsumption);

            // Move to the next week
            currentStart = currentStart.plusWeeks(1);
            currentEnd = currentEnd.plusWeeks(1);
        }
    }

    private static void generateMonthlyReport(Set<User> activeUsers, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> aggregatedMonthlyConsumption = new HashMap<>();
        LocalDate currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());

        // Adjust endDate to the last day of the month if it's not already
        endDate = endDate.with(TemporalAdjusters.lastDayOfMonth());

        while (!currentStart.isAfter(endDate)) {
            final LocalDate monthStart = currentStart;
            final LocalDate monthEnd = currentEnd;

            double monthlyConsumption = activeUsers.stream()
                    .mapToDouble(user -> user.getConsumption().getTotalCarbonForMonth(monthStart))
                    .sum();

            // Print start and end of the month
            System.out.println("Month from " + monthStart + " to " + monthEnd + ": " + monthlyConsumption);

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
