package edu.metrostate.brewcafe.view;

import edu.metrostate.brewcafe.controller.BaristaController;
import edu.metrostate.brewcafe.controller.MainMenuController;
import edu.metrostate.brewcafe.service.OrderService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;
import edu.metrostate.brewcafe.model.OrderStatus;
import edu.metrostate.brewcafe.model.Pastry;

public class BaristaLoginView {

    private final BorderPane rootLayout;

    public BaristaLoginView(BorderPane rootLayout) {
        this.rootLayout = rootLayout;
    }

    public Parent build() {
        // Header
        Label titleLabel = new Label("Login To Your Account");
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 50));

        // Error Message Bar
        Label errorText = new Label("Incorrect username or password entered. Please try again.");
        errorText.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        HBox errorBar = new HBox(errorText);
        errorBar.setAlignment(Pos.CENTER);
        errorBar.setPadding(new Insets(5, 20, 5, 20));
        errorBar.setMaxWidth(500);
        errorBar.setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb; " +
                "-fx-background-radius: 15; -fx-border-radius: 15;");
        // Hidden until fail
        errorBar.setVisible(false);

        // Username Input
        VBox usernameBox = new VBox(5);
        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("System", 24));
        TextField usernameField = new TextField();
        usernameField.setPrefHeight(45);
        usernameField.setStyle("-fx-background-color: #d3d3d3; -fx-background-radius: 20; " +
                "-fx-border-color: #8b7355; -fx-border-radius: 20;");
        usernameBox.getChildren().addAll(userLabel, usernameField);

        // Password Input
        VBox passwordBox = new VBox(5);
        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("System", 24));
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-background-color: #d3d3d3; -fx-background-radius: 20; " +
                "-fx-border-color: #8b7355; -fx-border-radius: 20;");
        passwordBox.getChildren().addAll(passLabel, passwordField);

        // Buttons
        Button loginButton = new Button("LOGIN");
        loginButton.setPrefSize(140, 45);
        loginButton.setStyle("-fx-background-color: #6f4e37; -fx-text-fill: white; " +
                "-fx-background-radius: 25; -fx-font-family: 'Comic Sans MS'; " +
                "-fx-font-style: italic; -fx-font-size: 18px;");

        Button backButton = new Button("Back");
        backButton.setPrefSize(140, 40);
        backButton.setStyle("-fx-background-color: white; -fx-border-color: #6f4e37; " +
                "-fx-background-radius: 25; -fx-border-radius: 25; " +
                "-fx-font-family: 'Comic Sans MS'; -fx-font-size: 16px;");

        backButton.setOnAction(e -> {
            // Uses the 1-parameter constructor you have in MainMenuController
            new MainMenuController(rootLayout).returnToHome();
        });

        loginButton.setOnAction(e -> {

            String user = usernameField.getText();
            String pass = passwordField.getText();

            BaristaController controller = new BaristaController();

            // Credentials check using the JSON-backed controller
            if (controller.authenticate(user, pass)) {
                errorBar.setVisible(false);

                // In a fully integrated app, the OrderService would be passed down from main.
                // For now, we create it here so the dashboard has the required data source.
                OrderService sharedOrderService = new OrderService();

                Order order1 = new Order("001", "Alice", OrderStatus.PENDING);
                order1.getItems().add(new OrderItem(new Beverage("bev-latte", "Latte", 4.50), 1));
                order1.getItems().add(new OrderItem(new Pastry("pas-muffin", "Muffin", 3.45, "Blueberry"), 2));
                sharedOrderService.addOrder(order1);

                Order order2 = new Order("002", "Marcus", OrderStatus.PENDING);
                order2.getItems().add(new OrderItem(new Beverage("bev-tea", "Green Tea", 3.25), 1));
                sharedOrderService.addOrder(order2);

                Order order3 = new Order("003", "Sara", OrderStatus.IN_PROGRESS);
                order3.getItems().add(new OrderItem(new Beverage("bev-latte", "Latte", 4.50), 2));
                sharedOrderService.addOrder(order3);

                // Switch to the actual Barista Dashboard instead of the placeholder
                BaristaDashboardView dashboard = new BaristaDashboardView(rootLayout, sharedOrderService);
                rootLayout.setCenter(dashboard.build());

                System.out.println("Login Successful - Moving to Dashboard");
            } else {
                errorBar.setVisible(true);
            }
        });

        // 6. Final Layout Assembly
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