package com.carbontracker;

public class User {
    private final String id; // Made final to ensure immutability
    private String name;
    private int age;
    private final Consumption consumption; // Add Consumption to User

    public User(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.consumption = new Consumption(); // Initialize Consumption
    }

    // Getters and Setters
    public String getId() {
        return id;
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
        this.age = age;
    }

    public Consumption getConsumption() {
        return consumption;
    }

    public void displayDetails() {
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Total Carbon Consumption: " + consumption.getTotalCarbon());
    }
}
