package com.carbontracker.impl;

import com.carbontracker.mapper.ConsumptionMapper;
import com.carbontracker.models.Consumption;
import com.carbontracker.models.Food;
import com.carbontracker.models.Housing;
import com.carbontracker.models.Transport;
import com.carbontracker.config.DbConnection;
import com.carbontracker.repository.ConsumptionRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConsumptionRepositoryImpl implements ConsumptionRepository {
    private final Connection connection;

    public ConsumptionRepositoryImpl() {
        this.connection = DbConnection.getConnection();
    }

    @Override
    public Optional<Consumption> findById(UUID id) {
        String query = "SELECT * FROM consumptions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, id); // Use setObject for UUID
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(ConsumptionMapper.mapRowToConsumption(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return Optional.empty();
    }

    @Override
    public Collection<Consumption> findAll() {
        List<Consumption> consumptions = new ArrayList<>();
        String query = "SELECT * FROM consumptions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                consumptions.add(ConsumptionMapper.mapRowToConsumption(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return consumptions;
    }

    @Override
    public Optional<Consumption> add(Consumption consumption) {
        String query = "INSERT INTO consumptions (id, amount, impact, start_date, end_date, type, user_id) " +
                "VALUES (?, ?, ?, ?, ?, CAST(? AS consumption_type), ?)";
        String foodQuery = "INSERT INTO food (id, food_type, weight) VALUES (?, ?, ?)";
        String housingQuery = "INSERT INTO housing (id, energy_type, energy_consumption) VALUES (?, ?, ?)";
        String transportQuery = "INSERT INTO transport (id, vehicle_type, distance_travelled) VALUES (?, ?, ?)";

        try {
            // Start transaction
            connection.setAutoCommit(false);

            // Insert into consumptions
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setObject(1, consumption.getId());
                stmt.setDouble(2, consumption.getAmount());
                stmt.setDouble(3, consumption.calculateImpact());
                stmt.setDate(4, Date.valueOf(consumption.getStartDate()));
                stmt.setDate(5, Date.valueOf(consumption.getEndDate()));
                stmt.setString(6, consumption.getType().name());
                stmt.setObject(7, consumption.getUserId());
                int rowsAffected = stmt.executeUpdate();

                // Check if insertion was successful
                if (rowsAffected == 0) {
                    connection.rollback(); // Rollback if not inserted
                    return Optional.empty();
                }
            }

            // If it's a Food instance, insert into food table
            if (consumption instanceof Food) {
                Food food = (Food) consumption;
                try (PreparedStatement stmt = connection.prepareStatement(foodQuery)) {
                    stmt.setObject(1, food.getId());
                    stmt.setString(2, food.getTypeFood());
                    stmt.setDouble(3, food.getWeight());
                    int foodRowsAffected = stmt.executeUpdate();

                    // Check if insertion was successful
                    if (foodRowsAffected == 0) {
                        connection.rollback(); // Rollback if not inserted
                        return Optional.empty();
                    }
                }
            }
            // If it's a Housing instance, insert into housing table
            else if (consumption instanceof Housing) {
                Housing housing = (Housing) consumption;
                try (PreparedStatement stmt = connection.prepareStatement(housingQuery)) {
                    stmt.setObject(1, housing.getId());
                    stmt.setString(2, housing.getTypeEnergy());
                    stmt.setDouble(3, housing.getConsumptionEnergy());
                    int housingRowsAffected = stmt.executeUpdate();

                    // Check if insertion was successful
                    if (housingRowsAffected == 0) {
                        connection.rollback(); // Rollback if not inserted
                        return Optional.empty();
                    }
                }
            }
            // If it's a Transport instance, insert into transport table
            else if (consumption instanceof Transport) {
                Transport transport = (Transport) consumption;
                try (PreparedStatement stmt = connection.prepareStatement(transportQuery)) {
                    stmt.setObject(1, transport.getId());
                    stmt.setString(2, transport.getVehicleType());
                    stmt.setDouble(3, transport.getDistanceTravelled());
                    int transportRowsAffected = stmt.executeUpdate();

                    // Check if insertion was successful
                    if (transportRowsAffected == 0) {
                        connection.rollback(); // Rollback if not inserted
                        return Optional.empty();
                    }
                }
            }

            // Commit the transaction if everything was successful
            connection.commit();
            return Optional.of(consumption); // Return the inserted consumption

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace(); // Handle rollback error
            }
            return Optional.empty(); // Return empty Optional on error
        } finally {
            // Reset auto-commit mode to true
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace(); // Handle error in resetting auto-commit
            }
        }
    }




    @Override
    public Optional<Consumption> update(UUID id, double amount, LocalDate startDate, LocalDate endDate) {
        String query = "UPDATE consumptions SET amount = ?, impact = ?, start_date = ?, end_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Calculate impact for the updated consumption
            Optional<Consumption> currentConsumptionOpt = findById(id);
            if (currentConsumptionOpt.isPresent()) {
                Consumption currentConsumption = currentConsumptionOpt.get();
                double impact = currentConsumption.calculateImpact();
                stmt.setDouble(1, amount);
                stmt.setDouble(2, impact); // Update the impact
                stmt.setDate(3, Date.valueOf(startDate));
                stmt.setDate(4, Date.valueOf(endDate));
                stmt.setObject(5, id); // Use setObject for UUID
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    return findById(id); // Return the updated consumption
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(UUID id) {
        String query = "DELETE FROM consumptions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, id); // Use setObject for UUID
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return false;
    }


}
