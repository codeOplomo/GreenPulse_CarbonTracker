/*package com.carbontracker.services;

import com.carbontracker.models.Consumption;
import com.carbontracker.models.User;
import com.carbontracker.models.enumeration.ConsumptionType;
import com.carbontracker.utils.UserInputHandler;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {
    private static final UserService userManager = UserService.getInstance();

    public static void userReport() {
        // Get the user ID as a UUID
        String idString = UserInputHandler.getValidString("Enter User ID: ");
        UUID userId;
        try {
            userId = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid User ID format.");
            return;
        }

        // Fetch the user using the UUID
        Optional<User> optionalUser = userManager.findById(userId);
        if (optionalUser.isEmpty()) {
            System.out.println("User not found.");
            return;
        }
        User user = optionalUser.get();

        int reportType = getReportType();
        LocalDate startDate = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
        LocalDate endDate = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");

        generateReportForUser(user, reportType, startDate, endDate);
    }

    private static void generateReportForUser(User user, int reportType, LocalDate startDate, LocalDate endDate) {
        switch (reportType) {
            case 1:
                generateDailyReport(user, startDate, endDate);
                break;
            case 2:
                generateWeeklyReport(user, startDate, endDate);
                break;
            case 3:
                generateMonthlyReport(user, startDate, endDate);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    public static void allUsersReport() {
        LocalDate startDate = UserInputHandler.getValidDate("Enter start date (YYYY-MM-DD): ");
        LocalDate endDate = UserInputHandler.getValidDate("Enter end date (YYYY-MM-DD): ");
        int reportType = getReportType();

        generateReportForAllUsers(reportType, startDate, endDate);
    }

    private static void generateReportForAllUsers(int reportType, LocalDate startDate, LocalDate endDate) {
        Set<User> activeUsers = userManager.findAll().stream()
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

    private static void generateDailyReport(Set<User> users, LocalDate startDate, LocalDate endDate) {
        List<Consumption> consumptions = users.stream()
                .flatMap(user -> user.getConsumptions().stream())
                .collect(Collectors.toList());

        generateDailyReportForConsumptions(consumptions, startDate, endDate);
    }

    private static void generateDailyReport(User user, LocalDate startDate, LocalDate endDate) {
        List<Consumption> consumptions = user.getConsumptions();
        generateDailyReportForConsumptions(consumptions, startDate, endDate);
    }

    private static void generateDailyReportForConsumptions(List<Consumption> consumptions, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> aggregatedDailyConsumption = new HashMap<>();

        for (Consumption consumption : consumptions) {
            Map<LocalDate, Double> dailyConsumptions = consumption.getDailyConsumptions();
            dailyConsumptions.forEach((date, amount) -> {
                if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                    aggregatedDailyConsumption.merge(date, amount * consumption.calculerImpact(), Double::sum);
                }
            });
        }

        if (aggregatedDailyConsumption.isEmpty()) {
            System.out.println("No consumption data available for the specified date range.");
            return;
        }

        System.out.println("Daily Consumption Report:");
        aggregatedDailyConsumption.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    private static void generateWeeklyReport(Set<User> users, LocalDate startDate, LocalDate endDate) {
        List<Consumption> consumptions = users.stream()
                .flatMap(user -> user.getConsumptions().stream())
                .collect(Collectors.toList());

        generateWeeklyReportForConsumptions(consumptions, startDate, endDate);
    }

    private static void generateWeeklyReport(User user, LocalDate startDate, LocalDate endDate) {
        List<Consumption> consumptions = user.getConsumptions();
        generateWeeklyReportForConsumptions(consumptions, startDate, endDate);
    }

    private static void generateWeeklyReportForConsumptions(List<Consumption> consumptions, LocalDate startDate, LocalDate endDate) {
        LocalDate currentStart = startDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate currentEnd = currentStart.plusWeeks(1).minusDays(1);
        endDate = endDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        while (!currentStart.isAfter(endDate)) {
            final LocalDate weekStart = currentStart;
            final LocalDate weekEnd = currentEnd;

            double weeklyConsumption = consumptions.stream()
                    .mapToDouble(consumption ->
                            consumption.getDailyConsumptions().entrySet().stream()
                                    .filter(entry -> !entry.getKey().isBefore(weekStart) && !entry.getKey().isAfter(weekEnd))
                                    .mapToDouble(entry -> entry.getValue() * consumption.calculerImpact())
                                    .sum()
                    )
                    .sum();

            System.out.println("Week from " + weekStart + " to " + weekEnd + ": " + weeklyConsumption);

            // Move to the next week
            currentStart = currentStart.plusWeeks(1);
            currentEnd = currentEnd.plusWeeks(1);
        }
    }

    public static void generateMonthlyReport(Set<User> users, LocalDate startDate, LocalDate endDate) {
        List<Consumption> consumptions = users.stream()
                .flatMap(user -> user.getConsumptions().stream())
                .collect(Collectors.toList());

        generateMonthlyReportForConsumptions(consumptions, startDate, endDate);
    }

    public static void generateMonthlyReport(User user, LocalDate startDate, LocalDate endDate) {
        List<Consumption> consumptions = user.getConsumptions();
        generateMonthlyReportForConsumptions(consumptions, startDate, endDate);
    }

    private static void generateMonthlyReportForConsumptions(List<Consumption> consumptions, LocalDate startDate, LocalDate endDate) {
        LocalDate currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
        endDate = endDate.with(TemporalAdjusters.lastDayOfMonth());

        if (currentEnd.isBefore(startDate)) {
            currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
            currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
        }

        while (!currentStart.isAfter(endDate)) {
            final LocalDate startOfMonth = currentStart;
            final LocalDate endOfMonth = currentEnd;

            double monthlyConsumption = consumptions.stream()
                    .mapToDouble(consumption ->
                            consumption.getDailyConsumptions().entrySet().stream()
                                    .filter(entry -> !entry.getKey().isBefore(startOfMonth) && !entry.getKey().isAfter(endOfMonth))
                                    .mapToDouble(entry -> entry.getValue() * consumption.calculerImpact())
                                    .sum()
                    )
                    .sum();

            System.out.println("Month from " + startOfMonth + " to " + endOfMonth + ": " + monthlyConsumption);

            // Move to the next month
            currentStart = currentStart.plusMonths(1);
            currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
        }
    }

    private static boolean hasConsumptionInDateRange(User user, LocalDate startDate, LocalDate endDate) {
        List<Consumption> consumptions = user.getConsumptions();
        LocalDate currentStart = startDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());

        endDate = endDate.with(TemporalAdjusters.lastDayOfMonth());

        while (!currentStart.isAfter(endDate)) {
            final LocalDate startOfMonth = currentStart;
            final LocalDate endOfMonth = currentEnd;

            double monthlyConsumption = consumptions.stream()
                    .mapToDouble(consumption -> {
                        return consumption.getDailyConsumptions().entrySet().stream()
                                .filter(entry -> !entry.getKey().isBefore(startOfMonth) && !entry.getKey().isAfter(endOfMonth))
                                .mapToDouble(entry -> entry.getValue() * consumption.calculerImpact())
                                .sum();
                    })
                    .sum();

            if (monthlyConsumption > 0) {
                return true;
            }

            currentStart = currentStart.plusMonths(1);
            currentEnd = currentStart.with(TemporalAdjusters.lastDayOfMonth());
        }

        return false;
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
*/