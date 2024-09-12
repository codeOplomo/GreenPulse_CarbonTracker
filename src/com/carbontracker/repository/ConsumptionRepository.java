package com.carbontracker.repository;

import com.carbontracker.models.Consumption;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ConsumptionRepository {
    Optional<Consumption> findById(UUID id);
    Collection<Consumption> findAll();
    Optional<Consumption> add(Consumption consumption);
    Optional<Consumption> update(UUID id, double amount, LocalDate startDate, LocalDate endDate);
    boolean delete(UUID id);
}
