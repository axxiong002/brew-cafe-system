package edu.metrostate.brewcafe.controller;

import edu.metrostate.brewcafe.service.CafeApplicationState;
import edu.metrostate.brewcafe.view.BaristaLoginView;
import edu.metrostate.brewcafe.view.CustomerDashboardView;
import edu.metrostate.brewcafe.view.ManagerLoginView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// Builds the role entry screens for the three cafe workflows.
public final class RolePlaceholderFactory {

    private RolePlaceholderFactory() {
    }

    public static Node createHomePlaceholder(BorderPane rootLayout, CafeApplicationState applicationState) {
        MainMenuController controller = new MainMenuController(rootLayout, applicationState);

        Label titleLabel = new Label("Brew Cafe System");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Choose a role to place orders, fulfill orders, or manage the cafe.");
        subtitleLabel.setStyle("-fx-font-size: 14px;");

        Button customerButton = new Button("Customer");
        customerButton.setPrefWidth(180);
        customerButton.setPrefHeight(36);
        customerButton.setOnAction(event -> controller.showCustomerScreen());

        Button baristaButton = new Button("Barista");
        baristaButton.setPrefWidth(180);
        baristaButton.setPrefHeight(36);
        baristaButton.setOnAction(event -> controller.showBaristaScreen());

        Button managerButton = new Button("Manager");
        managerButton.setPrefWidth(180);
        managerButton.setPrefHeight(36);
        managerButton.setOnAction(event -> controller.showManagerScreen());

        VBox content = new VBox(18, titleLabel, subtitleLabel, customerButton, baristaButton, managerButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        return content;
    }

    public static Node createCustomerPlaceholder(BorderPane rootLayout, CafeApplicationState applicationState) {
        return new CustomerDashboardView(rootLayout, applicationState).build();
    }

    public static Node createBaristaPlaceholder(BorderPane rootLayout, CafeApplicationState applicationState) {
        return new BaristaLoginView(rootLayout, applicationState).build();
    }

    public static Node createManagerPlaceholder(BorderPane rootLayout, CafeApplicationState applicationState) {
        return new ManagerLoginView(rootLayout, applicationState).build();
    }

}
