package edu.metrostate.brewcafe.view;

import edu.metrostate.brewcafe.controller.BaristaController;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

// Builds the barista login screen with inline status feedback.
public class BaristaLoginView {

    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;

    public BaristaLoginView(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;
    }

    public Parent build() {
        BaristaController controller = new BaristaController(rootLayout, applicationState);

        Label titleLabel = new Label("Barista Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label hintLabel = new Label("Sign in to view and fulfill customer orders.");

        Label errorText = new Label("Invalid barista credentials.");
        errorText.setStyle("-fx-text-fill: #8b2d2d; -fx-font-size: 13px;");

        HBox errorBar = new HBox(errorText);
        errorBar.setAlignment(Pos.CENTER);
        errorBar.setPadding(new Insets(6, 16, 6, 16));
        errorBar.setMaxWidth(320);
        errorBar.setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb; "
                + "-fx-background-radius: 8; -fx-border-radius: 8;");
        errorBar.setVisible(false);

        VBox usernameBox = new VBox(5);
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(260);
        usernameBox.getChildren().addAll(userLabel, usernameField);

        VBox passwordBox = new VBox(5);
        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(260);
        passwordBox.getChildren().addAll(passLabel, passwordField);

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(140);

        Button backButton = new Button("Back to Home");
        backButton.setPrefWidth(140);

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

        VBox inputContainer = new VBox(14, usernameBox, passwordBox);
        inputContainer.setMaxWidth(260);

        VBox buttonContainer = new VBox(15, loginButton, backButton);
        buttonContainer.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(14, titleLabel, hintLabel, errorBar, inputContainer, buttonContainer);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setMaxWidth(420);

        BorderPane wrapper = new BorderPane();
        wrapper.setCenter(mainLayout);
        return wrapper;
    }
}
