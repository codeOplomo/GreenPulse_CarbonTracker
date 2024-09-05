package com.carbontracker.services;

import com.carbontracker.models.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final UserManager INSTANCE = new UserManager();
    private final Map<String, User> users = new HashMap<>();

    private UserManager() { }

    public static UserManager getInstance() {
        return INSTANCE;
    }

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
}
