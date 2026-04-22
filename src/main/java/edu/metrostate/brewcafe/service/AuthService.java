package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.User;
import edu.metrostate.brewcafe.model.UserRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Centralizes shared credential checks so manager and barista logins use the same rules.
public class AuthService {
    private final List<User> users;

    public AuthService(List<User> users) {
        this.users = new ArrayList<>(users);
    }

    public Optional<User> authenticate(String username, String password, UserRole requiredRole) {
        return users.stream()
                .filter(user -> user.getRole() == requiredRole)
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }
}
