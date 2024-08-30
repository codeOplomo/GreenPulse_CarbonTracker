package com.carbontracker;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class UserManager {
    private final Map<String, User> users = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public void removeUser(String userId) {
        users.remove(userId);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public void updateUser(String userId, String name, int age) {
        User user = users.get(userId);
        if (user != null) {
            user.setName(name);
            user.setAge(age);
        }
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public double getTotalCarbonConsumptionForUser(String userId) {
        User user = getUser(userId);
        if (user != null) {
            return user.getConsumption().getTotalCarbon();
        }
        return 0.0;
    }

    public Map<String, Double> getTotalCarbonConsumptionForAllUsers() {
        return users.values().stream()
                .collect(Collectors.toMap(User::getId, user -> user.getConsumption().getTotalCarbon()));
    }

    public List<Consumption.ConsumptionEntry> getConsumptionEntriesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return users.values().stream()
                .flatMap(user -> user.getConsumption().getEntries().stream())
                .filter(entry -> !entry.getStartDate().isBefore(startDate) && !entry.getEndDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    // New method to generate a report for an individual user
    public void generateUserReport(String userId) {
        User user = getUser(userId);
        if (user != null) {
            System.out.println("Report for User: " + user.getId());
            user.displayDetails();
            System.out.println("Detailed Consumption Entries:");
            user.getConsumption().getEntries().forEach(entry -> {
                System.out.println("Carbon Amount: " + entry.getCarbonAmount() +
                        ", Start Date: " + entry.getStartDate() +
                        ", End Date: " + entry.getEndDate());
            });
        } else {
            System.out.println("User not found.");
        }
    }

    // New method to generate a summary report for all users
    public void generateAllUsersReport() {
        System.out.println("Summary Report for All Users:");
        getTotalCarbonConsumptionForAllUsers().forEach((userId, totalCarbon) -> {
            System.out.println("User ID: " + userId + ", Total Carbon Consumption: " + totalCarbon);
        });
    }

    // New method to generate a report for consumption between specific dates
    public void generateDateRangeReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Consumption.ConsumptionEntry> entries = getConsumptionEntriesBetweenDates(startDate, endDate);
        System.out.println("Consumption Report from " + startDate + " to " + endDate + ":");
        entries.forEach(entry -> {
            System.out.println("User ID: " + entry.getUserId() +
                    ", Carbon Amount: " + entry.getCarbonAmount() +
                    ", Start Date: " + entry.getStartDate() +
                    ", End Date: " + entry.getEndDate());
        });
    }
}
