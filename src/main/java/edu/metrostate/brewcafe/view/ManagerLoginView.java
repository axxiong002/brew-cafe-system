package edu.metrostate.brewcafe.view;

import edu.metrostate.brewcafe.controller.ManagerController;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// Builds the manager login screen with inline status feedback.
public class ManagerLoginView {
    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;

    public ManagerLoginView(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;
    }

    public Parent build() {
        ManagerController controller = new ManagerController(rootLayout, applicationState);

        Label titleLabel = new Label("Manager Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label hintLabel = new Label("Sign in to access menu and inventory tools.");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(260);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(260);

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-text-fill: #8b2d2d;");

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(140);
        loginButton.setOnAction(event ->
                controller.handleManagerLogin(usernameField.getText(), passwordField.getText(), statusLabel)
        );

        Button backButton = new Button("Back to Home");
        backButton.setPrefWidth(140);
        backButton.setOnAction(event -> controller.returnToHome());

        VBox content = new VBox(14, titleLabel, hintLabel, usernameField, passwordField, statusLabel, loginButton, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setMaxWidth(420);

        BorderPane wrapper = new BorderPane();
        wrapper.setCenter(content);
        return wrapper;
    }
}
