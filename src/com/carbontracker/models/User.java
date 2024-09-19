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
    private List<Consumption> consumptions;

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

    public void setConsumptions(List<Consumption> consumptions) {
        this.consumptions = consumptions;
    }

    // Display user details and consumption impacts

    public void displayConsumptions() {
        System.out.println("Consumptions:");
        for (Consumption consumption : consumptions) {
            System.out.println(" - " + consumption.getAmount());
        }
    }

    public void displayDetails() {
        System.out.println("User ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        displayConsumptions(); // Show individual consumptions
    }


    // Helper method to determine the type of consumption
    private ConsumptionType getConsumptionType(Consumption consumption) {
        if (consumption instanceof Transport) return ConsumptionType.TRANSPORT;
        if (consumption instanceof Housing) return ConsumptionType.HOUSING;
        if (consumption instanceof Food) return ConsumptionType.FOOD;
        throw new IllegalArgumentException("Unknown consumption type.");
    }
}
