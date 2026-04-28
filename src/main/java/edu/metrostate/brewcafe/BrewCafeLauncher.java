package edu.metrostate.brewcafe;

// Plain launcher class used by the packaged jar.
// Java can have trouble launching a JavaFX Application subclass directly from a shaded jar.
public final class BrewCafeLauncher {
    private BrewCafeLauncher() {
    }

    public static void main(String[] args) {
        BrewCafeApplication.main(args);
    }
}
