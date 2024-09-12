package com.carbontracker.impl;

import com.carbontracker.models.User;
import com.carbontracker.config.DbConnection;
import com.carbontracker.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {
    private final Connection connection;

    public UserRepositoryImpl() {
        this.connection = DbConnection.getConnection();
    }

    @Override
    public Optional<User> findById(UUID userId) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, userId); // Use setObject for UUID
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> findAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return users;
    }

    @Override
    public Optional<User> add(User user) {
        String query = "INSERT INTO users (id, name, age) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, user.getId()); // Use setObject for UUID
            stmt.setString(2, user.getName());
            stmt.setInt(3, user.getAge());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> update(UUID userId, String name, int age) {
        String query = "UPDATE users SET name = ?, age = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setObject(3, userId); // Use setObject for UUID
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return findById(userId); // Return the updated user
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(UUID userId) {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, userId); // Use setObject for UUID
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Consider proper logging
        }
        return false;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id"); // Cast to UUID
        String name = rs.getString("name");
        int age = rs.getInt("age");
        return new User(id, name, age);
    }
}
