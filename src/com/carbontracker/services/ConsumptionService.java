package com.carbontracker.services;

import com.carbontracker.models.Consumption;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

public class ConsumptionService {
    // Step 1: Create a private static instance of the class
    private static ConsumptionService instance;

    // Step 2: Make the constructor private so that this class cannot be instantiated from outside
    private ConsumptionService() {}

    // Step 3: Provide a public static method to get the instance of the class
    public static synchronized ConsumptionService getInstance() {
        if (instance == null) {
            instance = new ConsumptionService();
        }
        return instance;
    }

    public void addEntry(Consumption consumption, double totalCarbonAmount, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate;
        LocalDate end = endDate;
        long daysCount = ChronoUnit.DAYS.between(start, end) + 1; // Number of days inclusive

        // Calculate the daily amount
        double dailyAmount = totalCarbonAmount / daysCount;

        // Add the daily amount to each date in the range
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            consumption.getDailyConsumptions().merge(date, dailyAmount, Double::sum);
        }
    }


    public double getTotalCarbon(Consumption consumption) {
        return consumption.getDailyConsumptions().values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getTotalCarbonForWeek(Consumption consumption, LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusWeeks(1).minusDays(1);
        return consumption.getDailyConsumptions().entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startOfWeek) && !entry.getKey().isAfter(endOfWeek))
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }

    public double getTotalCarbonForMonth(Consumption consumption, LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        return consumption.getDailyConsumptions().entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startOfMonth) && !entry.getKey().isAfter(endOfMonth))
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }
}
