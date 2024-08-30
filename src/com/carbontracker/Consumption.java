package com.carbontracker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Consumption {
    private final List<ConsumptionEntry> entries;

    public Consumption() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(String userId, double carbonAmount, LocalDateTime startDate, LocalDateTime endDate) {
        entries.add(new ConsumptionEntry(userId, carbonAmount, startDate, endDate));
    }

    public List<ConsumptionEntry> getEntries() {
        return entries;
    }

    public double getTotalCarbon() {
        return entries.stream().mapToDouble(ConsumptionEntry::getCarbonAmount).sum();
    }

    public static class ConsumptionEntry {
        private final String userId;
        private final double carbonAmount;
        private final LocalDateTime startDate;
        private final LocalDateTime endDate;

        public ConsumptionEntry(String userId, double carbonAmount, LocalDateTime startDate, LocalDateTime endDate) {
            this.userId = userId;
            this.carbonAmount = carbonAmount;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getUserId() {
            return userId;
        }

        public double getCarbonAmount() {
            return carbonAmount;
        }

        public LocalDateTime getStartDate() {
            return startDate;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }
    }
}
