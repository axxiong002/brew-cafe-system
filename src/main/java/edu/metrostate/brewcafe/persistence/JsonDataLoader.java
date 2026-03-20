package edu.metrostate.brewcafe.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Simple file loader placeholder for moving JSON read logic out of the UI layer.
public class JsonDataLoader {
    public String loadJson(Path path) throws IOException {
        // Keeps file loading isolated so JSON parsing can stay out of UI code later.
        return Files.readString(path);
    }
}
