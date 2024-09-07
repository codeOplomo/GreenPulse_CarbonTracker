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

    public static void userReport() {
        String id = UserInputHandler.getValidString("Enter User ID: ");
        User user = userManager.getUser(id);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        int reportType = getReportType();
        LocalDate startDate = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
        LocalDate endDate = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");

        generateReportForUser(user.getConsumption(), reportType, startDate, endDate);
    }

    public static void allUsersReport() {
        LocalDate startDate = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
        LocalDate endDate = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");
        int reportType = getReportType();

        generateReportForAllUsers(reportType, startDate, endDate);
    }

    private static void generateReportForUser(Consumption consumption, int reportType, LocalDate startDate, LocalDate endDate) {
        switch (reportType) {
            case 1:
                generateDailyReport(consumption, startDate, endDate);
                break;
            case 2:
                generateWeeklyReport(consumption, startDate, endDate);
                break;
            case 3:
                generateMonthlyReport(consumption, startDate, endDate);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void generateReportForAllUsers(int reportType, LocalDate startDate, LocalDate endDate) {
        Set<User> activeUsers = userManager.getAllUsers().stream()
                .filter(user -> hasConsumptionInDateRange(user, startDate, endDate))
                .collect(Collectors.toSet());

        if (activeUsers.isEmpty()) {
            System.out.println("No users with consumption data in the specified date range.");
            return;
        }

        System.out.println("Total users with consumption data between " + startDate + " and " + endDate + ": " + activeUsers.size());

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

    private static void generateDailyReport(Object data, LocalDate startDate, LocalDate endDate) {
        if (data instanceof Consumption) {
            Consumption consumption = (Consumption) data;
            Map<LocalDate, Double> dailyConsumptions = consumption.calculateDailyConsumptionAverages();

            if (dailyConsumptions.isEmpty()) {
                System.out.println("No consumption data available.");
                return;
            }

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

        } else if (data instanceof Set) {
            Set<User> activeUsers = (Set<User>) data;
            Map<LocalDate, Double> aggregatedDailyConsumption = new HashMap<>();

            for (User user : activeUsers) {
                Consumption consumption = user.getConsumption();
                Map<LocalDate, Double> dailyConsumptions = consumption.calculateDailyConsumptionAverages();

                dailyConsumptions.forEach((date, amount) -> {
                    if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                        aggregatedDailyConsumption.merge(date, amount, Double::sum);
                    }
                });
            }

            System.out.println("Aggregated Daily Consumption Report:");
            aggregatedDailyConsumption.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        } else {
            System.out.println("Invalid data type.");
        }
    }

    private static void generateWeeklyReport(Object data, LocalDate startDate, LocalDate endDate) {
        if (data instanceof Consumption) {
            Consumption consumption = (Consumption) data;
            LocalDate currentStart = startDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate currentEnd = currentStart.plusWeeks(1).minusDays(1);
            endDate = endDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

            while (!currentStart.isAfter(endDate)) {
                double weeklyConsumption = consumption.getTotalCarbonForWeek(currentStart);

                System.out.println("Week from " + currentStart + " to " + currentEnd + ": " + weeklyConsumption);

                currentStart = currentStart.plusWeeks(1);
                currentEnd = currentEnd.plusWeeks(1);
            }
        } else if (data instanceof Set) {
            Set<User> activeUsers = (Set<User>) data;
            LocalDate currentStart = startDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate currentEnd = currentStart.plusWeeks(1).minusDays(1);
            endDate = endDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
            Map<LocalDate, Double> aggregatedWeeklyConsumption = new HashMap<>();

            while (!currentStart.isAfter(endDate)) {
                final LocalDate weekStart = currentStart;
                final LocalDate weekEnd = currentEnd;

                double weeklyConsumption = activeUsers.stream()
                        .mapToDouble(user -> user.getConsumption().getTotalCarbonForWeek(weekStart))
                        .sum();

                aggregatedWeeklyConsumption.put(weekStart, weeklyConsumption);

                System.out.println("Week from " + weekStart + " to " + weekEnd + ": " + weeklyConsumption);

                currentStart = currentStart.plusWeeks(1);
                currentEnd = currentEnd.plusWeeks(1);
            }
        } else {
            System.out.println("Invalid data type.");
        }
    }

    private static void generateMonthlyReport(Object data, LocalDate startDate, LocalDate endDate) {
        if (data instanceof Consumption) {
            Consumption consumption = (Consumption) data;
            LocalDate currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
            endDate = endDate.with(TemporalAdjusters.lastDayOfMonth());

            if (currentEnd.isBefore(startDate)) {
                currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
                currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
            }

            while (!currentStart.isAfter(endDate)) {
                double monthlyConsumption = consumption.getTotalCarbonForMonth(currentStart);

                System.out.println("Month from " + currentStart + " to " + currentEnd + ": " + monthlyConsumption);

                currentStart = currentStart.plusMonths(1);
                currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
            }
        } else if (data instanceof Set) {
            Set<User> activeUsers = (Set<User>) data;
            LocalDate currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
            endDate = endDate.with(TemporalAdjusters.lastDayOfMonth());
            Map<LocalDate, Double> aggregatedMonthlyConsumption = new HashMap<>();

            while (!currentStart.isAfter(endDate)) {
                final LocalDate monthStart = currentStart;
                final LocalDate monthEnd = currentEnd;

                double monthlyConsumption = activeUsers.stream()
                        .mapToDouble(user -> user.getConsumption().getTotalCarbonForMonth(monthStart))
                        .sum();

                aggregatedMonthlyConsumption.put(monthStart, monthlyConsumption);

                System.out.println("Month from " + monthStart + " to " + monthEnd + ": " + monthlyConsumption);

                currentStart = currentStart.plusMonths(1);
                currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
            }
        } else {
            System.out.println("Invalid data type.");
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
}
