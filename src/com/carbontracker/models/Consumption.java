package com.carbontracker.models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Consumption {
    private final Map<LocalDate, Double> dailyConsumptions;

    public Consumption() {
        this.dailyConsumptions = new HashMap<>();
    }

    // Getters and Setters

    public Map<LocalDate, Double> getDailyConsumptions() {
        return dailyConsumptions;
    }


    public void setDailyConsumptions(Map<LocalDate, Double> dailyConsumptions) {
        this.dailyConsumptions.clear();
        this.dailyConsumptions.putAll(dailyConsumptions);
    }
}
