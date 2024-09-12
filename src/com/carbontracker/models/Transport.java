package com.carbontracker.models;

import com.carbontracker.models.enumeration.ConsumptionType;

import java.time.LocalDate;
import java.util.UUID;

public class Transport extends Consumption {
    private final double distanceTravelled;
    private final String vehicleType;

    public Transport(UUID id, String vehicleType, double distanceTravelled, double amount, LocalDate startDate, LocalDate endDate, UUID userId) {
        super(id, amount, ConsumptionType.TRANSPORT, startDate, endDate, userId);
        if (distanceTravelled <= 0) throw new IllegalArgumentException("Distance travelled must be positive.");
        this.distanceTravelled = distanceTravelled;
        this.vehicleType = vehicleType;
        // Impact is set in the parent class constructor via calculateImpact method
    }

    @Override
    public double calculateImpact() {
        double impactScale;
        switch (vehicleType.toLowerCase()) {
            case "car":
                impactScale = 0.5;
                break;
            case "train":
                impactScale = 0.1;
                break;
            default:
                throw new IllegalArgumentException("Unsupported vehicle type.");
        }
        return getAmount() * distanceTravelled * impactScale;
    }
}
