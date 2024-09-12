package com.carbontracker.impl;

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
                    return Optional.of(mapRowToConsumption(rs));
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
                consumptions.add(mapRowToConsumption(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return consumptions;
    }

    @Override
    public Optional<Consumption> add(Consumption consumption) {
        String query = "INSERT INTO consumptions (id, amount, impact, start_date, end_date, type, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, consumption.getId()); // Use setObject for UUID
            stmt.setDouble(2, consumption.getAmount());
            stmt.setDouble(3, consumption.calculateImpact()); // Calculate impact before insertion
            stmt.setDate(4, Date.valueOf(consumption.getStartDate()));
            stmt.setDate(5, Date.valueOf(consumption.getEndDate()));
            stmt.setString(6, consumption.getType().name());
            stmt.setObject(7, consumption.getUserId()); // Use setObject for UUID
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(consumption);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return Optional.empty();
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

    private Consumption mapRowToConsumption(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id"); // Retrieve UUID
        double amount = rs.getDouble("amount");
        double impact = rs.getDouble("impact"); // Read impact from result set
        LocalDate startDate = rs.getDate("start_date").toLocalDate();
        LocalDate endDate = rs.getDate("end_date").toLocalDate();
        UUID userId = (UUID) rs.getObject("user_id"); // Retrieve UUID
        String type = rs.getString("type");

        switch (type) {
            case "FOOD":
                return mapRowToFood(rs, id, amount, impact, startDate, endDate, userId);
            case "HOUSING":
                return mapRowToHousing(rs, id, amount, impact, startDate, endDate, userId);
            case "TRANSPORT":
                return mapRowToTransport(rs, id, amount, impact, startDate, endDate, userId);
            default:
                throw new SQLException("Unknown consumption type: " + type);
        }
    }

    private Food mapRowToFood(ResultSet rs, UUID id, double amount, double impact, LocalDate startDate, LocalDate endDate, UUID userId) throws SQLException {
        String typeFood = rs.getString("food_type");
        double weight = rs.getDouble("weight");

        return new Food(id, typeFood, weight, amount, startDate, endDate, userId);
    }

    private Housing mapRowToHousing(ResultSet rs, UUID id, double amount, double impact, LocalDate startDate, LocalDate endDate, UUID userId) throws SQLException {
        String typeEnergy = rs.getString("energy_type");
        double energyConsumption = rs.getDouble("energy_consumption");

        return new Housing(id, typeEnergy, energyConsumption, amount, startDate, endDate, userId);
    }

    private Transport mapRowToTransport(ResultSet rs, UUID id, double amount, double impact, LocalDate startDate, LocalDate endDate, UUID userId) throws SQLException {
        String typeVehicle = rs.getString("vehicle_type");
        double distanceTravelled = rs.getDouble("distance_travelled");

        return new Transport(id, typeVehicle, distanceTravelled, amount, startDate, endDate, userId);
    }
}
