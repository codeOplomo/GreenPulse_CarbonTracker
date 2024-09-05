package com.carbontracker.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

public class Consumption {
    private final Map<LocalDate, Double> dailyConsumptions;

    public Consumption() {
        this.dailyConsumptions = new HashMap<>();
    }

    public void addEntry(double totalCarbonAmount, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate;
        LocalDate end = endDate;
        long daysCount = ChronoUnit.DAYS.between(start, end) + 1; // Number of days inclusive

        // Calculate the daily amount
        double dailyAmount = totalCarbonAmount / daysCount;

        // Add the daily amount to each date in the range
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            dailyConsumptions.merge(date, dailyAmount, Double::sum);
        }
    }


    public double getTotalCarbon() {
        return dailyConsumptions.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getTotalCarbonForDay(LocalDate date) {
        return dailyConsumptions.getOrDefault(date, 0.0);
    }

    public double getTotalCarbonForWeek(LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusWeeks(1).minusDays(1);
        return dailyConsumptions.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startOfWeek) && !entry.getKey().isAfter(endOfWeek))
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }

    public double getTotalCarbonForMonth(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        return dailyConsumptions.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startOfMonth) && !entry.getKey().isAfter(endOfMonth))
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }

    public Map<LocalDate, Double> calculateDailyConsumptionAverages() {
        // Return the daily consumptions as is
        return new HashMap<>(dailyConsumptions);
    }
}
