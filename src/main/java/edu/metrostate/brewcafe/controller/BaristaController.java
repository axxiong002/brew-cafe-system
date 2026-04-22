package edu.metrostate.brewcafe.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;

public class BaristaController {

    private final ObjectMapper mapper = new ObjectMapper();

    public boolean authenticate(String username, String password) {
        try {
            // Read users.json into a tree of objects
            JsonNode root = mapper.readTree(
                    Path.of("data", "users.json").toFile()
            );

            // Get the "users" array
            JsonNode users = root.get("users");

            // Check each user one at a time
            for (JsonNode user : users) {
                String jsonUsername = user.get("username").asText();
                String jsonPassword = user.get("password").asText();
                String jsonRole     = user.get("role").asText();

                // All three must match the SAME entry
                if (jsonUsername.equals(username)
                        && jsonPassword.equals(password)
                        && jsonRole.equals("BARISTA")) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            System.err.println("Could not load users.json: " + e.getMessage());
            return false;
        }
    }
}