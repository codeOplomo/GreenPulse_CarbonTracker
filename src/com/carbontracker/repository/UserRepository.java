package com.carbontracker.repository;

import com.carbontracker.models.User;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID userId);
    Collection<User> findAll();
    Optional<User> add(User user);
    Optional<User> update(UUID userId, String name, int age);
    boolean delete(UUID userId);
}
