package com.carbontracker.models;

import com.carbontracker.models.enumeration.ConsumptionType;

import java.time.LocalDate;
import java.util.UUID;

public class Food extends Consumption {
    private final String typeFood;
    private final double weight;

    public Food(UUID id, String typeFood, double weight, double amount, LocalDate startDate, LocalDate endDate, UUID userId) {
        super(id, amount, ConsumptionType.FOOD, startDate, endDate, userId);
        if (weight <= 0) throw new IllegalArgumentException("Weight must be positive.");
        this.typeFood = typeFood;
        this.weight = weight;
        // Impact is set in the parent class constructor via calculateImpact method
    }

    @Override
    public double calculateImpact() {
        double impactScale;
        switch (typeFood.toLowerCase()) {
            case "meat":
                impactScale = 5.0;
                break;
            case "vegetable":
                impactScale = 0.5;
                break;
            default:
                throw new IllegalArgumentException("Unsupported food type.");
        }
        return getAmount() * weight * impactScale;
    }
}
