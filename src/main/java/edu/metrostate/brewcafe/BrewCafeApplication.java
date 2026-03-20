package edu.metrostate.brewcafe;

import edu.metrostate.brewcafe.view.MainMenuView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Main JavaFX entry point for the project starter.
// This launches the shared landing screen the team can build out by role.
public class BrewCafeApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        // This is the single JavaFX entry point for the project starter.
        MainMenuView mainMenuView = new MainMenuView();
        Scene scene = new Scene(mainMenuView.build(), 960, 640);

        primaryStage.setTitle("Brew Cafe System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
