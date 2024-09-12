package com.carbontracker.models;

import com.carbontracker.models.enumeration.ConsumptionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class User {
    private final UUID id; // Changed from String to UUID
    private String name;
    private int age;
    private final List<Consumption> consumptions;

    public User(UUID id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.consumptions = new ArrayList<>();
    }

    // Getters and Setters
    public UUID getId() {
        return id; // Changed to UUID
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) throw new IllegalArgumentException("Age cannot be negative.");
        this.age = age;
    }

    public List<Consumption> getConsumptions() {
        return consumptions;
    }

    // Display user details and consumption impacts
    public void displayDetails() {
        System.out.println("User ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);

        // Group consumptions by type
        Map<ConsumptionType, List<Consumption>> groupedConsumptions = consumptions.stream()
                .collect(Collectors.groupingBy(this::getConsumptionType));

        // Display grouped consumptions
        for (Map.Entry<ConsumptionType, List<Consumption>> entry : groupedConsumptions.entrySet()) {
            ConsumptionType type = entry.getKey();
            List<Consumption> consList = entry.getValue();
            System.out.println("\nConsumption Type: " + type);

            // Calculate and display total impact for this type
            double totalImpact = consList.stream().mapToDouble(Consumption::calculateImpact).sum();
            System.out.println("Total Impact: " + totalImpact);

            // Optionally, you can list all individual consumptions
            for (Consumption consumption : consList) {
                System.out.println(" - " + consumption.getClass().getSimpleName() + ": " + consumption.calculateImpact());
            }
        }
    }

    // Helper method to determine the type of consumption
    private ConsumptionType getConsumptionType(Consumption consumption) {
        if (consumption instanceof Transport) return ConsumptionType.TRANSPORT;
        if (consumption instanceof Housing) return ConsumptionType.HOUSING;
        if (consumption instanceof Food) return ConsumptionType.FOOD;
        throw new IllegalArgumentException("Unknown consumption type.");
    }
}
