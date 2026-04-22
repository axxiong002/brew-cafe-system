package edu.metrostate.brewcafe;

import edu.metrostate.brewcafe.service.CafeApplicationState;
import edu.metrostate.brewcafe.view.MainMenuView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Main JavaFX entry point for the project starter.
// This launches the shared landing screen the team can build out by role.
public class BrewCafeApplication extends Application {
    private CafeApplicationState applicationState;

    @Override
    public void start(Stage primaryStage) {
        applicationState = createApplicationState();

        // This is the single JavaFX entry point for the project starter.
        MainMenuView mainMenuView = new MainMenuView(applicationState);
        Scene scene = new Scene(mainMenuView.build(), 960, 640);

        primaryStage.setTitle("Brew Cafe System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (applicationState != null) {
            applicationState.saveAllData();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private CafeApplicationState createApplicationState() {
        try {
            return CafeApplicationState.fromDefaultData();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to initialize application data from JSON files.", exception);
        }
    }
}
