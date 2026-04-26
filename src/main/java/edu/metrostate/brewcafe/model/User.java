package edu.metrostate.brewcafe.model;

import java.util.Objects;

// Shared user model for hardcoded role-based login data loaded from JSON.
public class User {
    private String username;
    private String password;
    private UserRole role;

    public User() {
    }

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User other)) {
            return false;
        }
        return Objects.equals(username, other.username) && role == other.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, role);
    }
}
