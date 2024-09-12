package com.carbontracker.services;

import com.carbontracker.models.User;
import com.carbontracker.repository.UserRepository;
import com.carbontracker.impl.UserRepositoryImpl;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private static final UserService INSTANCE = new UserService();
    private final UserRepository userRepository = new UserRepositoryImpl(); // Use repository

    private UserService() { }

    public static UserService getInstance() {
        return INSTANCE;
    }

    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> add(User user) {
        return userRepository.add(user);
    }

    public boolean delete(UUID userId) {
        return userRepository.delete(userId);
    }

    public Optional<User> update(UUID userId, String name, int age) {
        return userRepository.update(userId, name, age);
    }
}
