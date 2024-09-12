package com.carbontracker.models;

import com.carbontracker.models.enumeration.ConsumptionType;

import java.time.LocalDate;
import java.util.UUID;

public class Housing extends Consumption {
    private final double consommationEnergy;
    private final String typeEnergy;

    public Housing(UUID id, String typeEnergy, double consommationEnergy, double amount, LocalDate startDate, LocalDate endDate, UUID userId) {
        super(id, amount, ConsumptionType.HOUSING, startDate, endDate, userId);
        if (consommationEnergy <= 0) throw new IllegalArgumentException("Consommation d'énergie must be positive.");
        this.consommationEnergy = consommationEnergy;
        this.typeEnergy = typeEnergy;
        // Impact is set in the parent class constructor via calculateImpact method
    }

    @Override
    public double calculateImpact() {
        double impactScale;
        switch (typeEnergy.toLowerCase()) {
            case "electricity":
                impactScale = 1.5;
                break;
            case "gas":
                impactScale = 2.0;
                break;
            default:
                throw new IllegalArgumentException("Unsupported energy type.");
        }
        return getAmount() * consommationEnergy * impactScale;
    }
}
