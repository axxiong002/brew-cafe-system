package edu.metrostate.brewcafe.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.metrostate.brewcafe.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Simple file loader placeholder for moving JSON read logic out of the UI layer.
public class JsonDataLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String loadJson(Path path) throws IOException {
        // Keeps file loading isolated so JSON parsing can stay out of UI code later.
        return Files.readString(path);
    }

    public List<User> loadUsers(Path path) throws IOException {
        UsersData usersData = objectMapper.readValue(path.toFile(), UsersData.class);
        return usersData.users();
    }

    private record UsersData(List<User> users) {
    }
}
