package edu.metrostate.brewcafe.view;

import edu.metrostate.brewcafe.controller.BaristaController;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// Build barista login screen
public class BaristaLoginView {

    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;

    public BaristaLoginView(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;
    }

    public Parent build() {
        BaristaController controller = new BaristaController(rootLayout, applicationState);

        Label titleLabel = new Label("Login To Your Account");
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 50));

        Label errorText = new Label("Incorrect username or password entered. Please try again.");
        errorText.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        HBox errorBar = new HBox(errorText);
        errorBar.setAlignment(Pos.CENTER);
        errorBar.setPadding(new Insets(5, 20, 5, 20));
        errorBar.setMaxWidth(500);
        errorBar.setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb; " +
                "-fx-background-radius: 15; -fx-border-radius: 15;");
        errorBar.setVisible(false);

        VBox usernameBox = new VBox(5);
        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("System", 24));
        TextField usernameField = new TextField();
        usernameField.setPrefHeight(45);
        usernameField.setStyle("-fx-background-color: #d3d3d3; -fx-background-radius: 20; -fx-border-color: #8b7355; -fx-border-radius: 20;");
        usernameBox.getChildren().addAll(userLabel, usernameField);

        VBox passwordBox = new VBox(5);
        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("System", 24));
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-background-color: #d3d3d3; -fx-background-radius: 20; -fx-border-color: #8b7355; -fx-border-radius: 20;");
        passwordBox.getChildren().addAll(passLabel, passwordField);

        Button loginButton = new Button("LOGIN");
        loginButton.setPrefSize(140, 45);
        loginButton.setStyle("-fx-background-color: #6f4e37; -fx-text-fill: white; -fx-background-radius: 25; -fx-font-family: 'Comic Sans MS'; -fx-font-style: italic; -fx-font-size: 18px;");

        Button backButton = new Button("Back");
        backButton.setPrefSize(140, 40);
        backButton.setStyle("-fx-background-color: white; -fx-border-color: #6f4e37; -fx-background-radius: 25; -fx-border-radius: 25; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 16px;");

        backButton.setOnAction(e -> controller.returnToHome());

        loginButton.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            if (controller.authenticate(user, pass)) {
                errorBar.setVisible(false);
                BaristaDashboardView dashboard = new BaristaDashboardView(rootLayout, applicationState, controller);
                rootLayout.setCenter(dashboard.build());
            } else {
                errorBar.setVisible(true);
            }
        });

        VBox inputContainer = new VBox(20, usernameBox, passwordBox);
        inputContainer.setMaxWidth(450);

        VBox buttonContainer = new VBox(15, loginButton, backButton);
        buttonContainer.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(30, titleLabel, errorBar, inputContainer, buttonContainer);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: white;");
        mainLayout.setPadding(new Insets(50));

        BorderPane wrapper = new BorderPane();
        wrapper.setStyle("-fx-background-color: white;");
        wrapper.setCenter(mainLayout);
        return wrapper;
    }
}