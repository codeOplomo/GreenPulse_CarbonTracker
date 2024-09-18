package com.carbontracker.models;

import com.carbontracker.models.enumeration.ConsumptionType;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Consumption {
    private UUID id;
    private double amount;
    private double impact; // Added impact property
    private LocalDate startDate;
    private LocalDate endDate;
    private ConsumptionType type;
    private UUID userId;

    public Consumption(UUID id, double amount, ConsumptionType type, LocalDate startDate, LocalDate endDate, UUID userId) {
        this.id = id;
        setAmount(amount);
        this.type = type;
        this.startDate = startDate != null ? startDate : LocalDate.now();
        this.endDate = endDate != null ? endDate : LocalDate.now();
        this.userId = userId;
    }

    // Getter and Setter for id
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    // Getter and Setter for userId
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    // Getter and Setter for amount
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative.");
        this.amount = amount;
    }

    // Getter and Setter for impact
    public double getImpact() {
        return impact;
    }

    public void setImpact(double impact) {
        if (impact < 0) throw new IllegalArgumentException("Impact cannot be negative.");
        this.impact = impact;
    }

    // Getter and Setter for startDate
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // Getter and Setter for endDate
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // Getter and Setter for type
    public ConsumptionType getType() {
        return type;
    }

    public void setType(ConsumptionType type) {
        this.type = type;
    }

    // Abstract method to be implemented by subclasses
    public abstract double calculateImpact();
}
