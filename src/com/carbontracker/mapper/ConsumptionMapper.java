package com.carbontracker.mapper;

import com.carbontracker.models.Consumption;
import com.carbontracker.models.Food;
import com.carbontracker.models.Housing;
import com.carbontracker.models.Transport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class ConsumptionMapper {
    public static Consumption mapRowToConsumption(ResultSet rs) throws SQLException {

        double impact = rs.getDouble("impact");
        UUID id = UUID.fromString(rs.getString("id"));
        double amount = rs.getDouble("amount");
        LocalDate startDate = rs.getDate("start_date").toLocalDate();
        LocalDate endDate = rs.getDate("end_date").toLocalDate();
        UUID userId = UUID.fromString(rs.getString("user_id"));
        String type = rs.getString("type");

        switch (type) {
            case "FOOD":
                return mapRowToFood(rs, id, amount, impact, startDate, endDate, userId);
            case "HOUSING":
                return mapRowToHousing(rs, id, amount, impact, startDate, endDate, userId);
            case "TRANSPORT":
                return mapRowToTransport(rs, id, amount, impact, startDate, endDate, userId);
            default:
                throw new SQLException("Unknown consumption type");
        }
    }


    private static Food mapRowToFood(ResultSet rs, UUID id, double amount, double impact, LocalDate startDate, LocalDate endDate, UUID userId) throws SQLException {
        String typeFood = rs.getString("food_type");
        double weight = rs.getDouble("weight");

        return new Food(id, typeFood, weight, amount, startDate, endDate, userId);
    }

    private static Housing mapRowToHousing(ResultSet rs, UUID id, double amount, double impact, LocalDate startDate, LocalDate endDate, UUID userId) throws SQLException {
        String typeEnergy = rs.getString("energy_type");
        double energyConsumption = rs.getDouble("energy_consumption");

        return new Housing(id, typeEnergy, energyConsumption, amount, startDate, endDate, userId);
    }

    private static Transport mapRowToTransport(ResultSet rs, UUID id, double amount, double impact, LocalDate startDate, LocalDate endDate, UUID userId) throws SQLException {
        String typeVehicle = rs.getString("vehicle_type");
        double distanceTravelled = rs.getDouble("distance_travelled");

        return new Transport(id, typeVehicle, distanceTravelled, amount, startDate, endDate, userId);
    }
}
