package com.carbontracker.models;

import com.carbontracker.models.enumeration.ConsumptionType;

import java.time.LocalDate;
import java.util.UUID;

public class Housing extends Consumption {
    private final double energyConsumption;
    private final String typeEnergy;

    public Housing(UUID id, String typeEnergy, double energyConsumption, double amount, LocalDate startDate, LocalDate endDate, UUID userId) {
        super(id, amount, ConsumptionType.HOUSING, startDate, endDate, userId);
        if (energyConsumption <= 0) throw new IllegalArgumentException("Consommation d'énergie must be positive.");
        this.energyConsumption = energyConsumption;
        this.typeEnergy = typeEnergy;
        this.setImpact(calculateImpact());
    }

    public double getConsumptionEnergy() {
        return energyConsumption;
    }

    public String getTypeEnergy() {
        return typeEnergy;
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
        return getAmount() * energyConsumption * impactScale;
    }
}
