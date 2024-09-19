package com.carbontracker.services;

import com.carbontracker.impl.ConsumptionRepositoryImpl;
import com.carbontracker.models.*;
import com.carbontracker.models.enumeration.ConsumptionType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ConsumptionService {
    private static ConsumptionService instance;
    private final ConsumptionRepositoryImpl consumptionRepository;

    private ConsumptionService() {
        this.consumptionRepository = new ConsumptionRepositoryImpl(); // Initialize repository
    }

    public static synchronized ConsumptionService getInstance() {
        if (instance == null) {
            instance = new ConsumptionService();
        }
        return instance;
    }

    public boolean addEntry(User user, Consumption consumption, LocalDate startDate, LocalDate endDate) {
        try {
            // Calculate daily amount if needed
            LocalDate start = startDate;
            LocalDate end = endDate;
            long daysCount = ChronoUnit.DAYS.between(start, end) + 1; // Number of days inclusive
            double dailyAmount = consumption.getAmount() / daysCount;


            // Set start and end dates for the consumption
            consumption.setStartDate(start);
            consumption.setEndDate(end);

            // Add the new consumption to the repository
            Optional<Consumption> addedConsumption = consumptionRepository.add(consumption);
            return addedConsumption.isPresent(); // Return true if successful

        } catch (Exception e) {
            // Handle the exception (e.g., log it)
            e.printStackTrace(); // Consider logging instead of printing stack trace
            return false; // Return false if an exception occurs
        }
    }
/*
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
    }*/
}
