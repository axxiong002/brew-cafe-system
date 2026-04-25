package edu.metrostate.brewcafe.controller;

import edu.metrostate.brewcafe.service.CafeApplicationState;
import edu.metrostate.brewcafe.view.ManagerLoginView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// Builds the temporary role screens so teammates can see where each workflow belongs.
public final class RolePlaceholderFactory {
    private RolePlaceholderFactory() {
    }

    public static Node createHomePlaceholder(BorderPane rootLayout, CafeApplicationState applicationState) {
        MainMenuController controller = new MainMenuController(rootLayout, applicationState);

        Label titleLabel = new Label("Brew Cafe System");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Starter shell for customer, barista, and manager workflows.");
        subtitleLabel.setStyle("-fx-font-size: 14px;");

        Button customerButton = new Button("Customer");
        customerButton.setPrefWidth(180);
        customerButton.setOnAction(event -> controller.showCustomerScreen());

        Button baristaButton = new Button("Barista");
        baristaButton.setPrefWidth(180);
        baristaButton.setOnAction(event -> controller.showBaristaScreen());

        Button managerButton = new Button("Manager");
        managerButton.setPrefWidth(180);
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
        return createRoleScreen(
                rootLayout,
                applicationState,
                "Barista Placeholder",
                "This is where the FIFO order queue, status updates, and fulfillment flow will go.",
                "Likely owner: barista workflow"
        );
    }

    public static Node createManagerPlaceholder(BorderPane rootLayout, CafeApplicationState applicationState) {
        return new ManagerLoginView(rootLayout, applicationState).build();
    }

    private static Node createRoleScreen(
            BorderPane rootLayout,
            CafeApplicationState applicationState,
            String title,
            String summary,
            String ownerHint
    ) {
        MainMenuController controller = new MainMenuController(rootLayout, applicationState);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label summaryLabel = new Label(summary);
        summaryLabel.setWrapText(true);

        Label ownerLabel = new Label(ownerHint);
        ownerLabel.setWrapText(true);
        ownerLabel.setStyle("-fx-font-style: italic;");

        Button backButton = new Button("Back to Home");
        backButton.setOnAction(event -> controller.returnToHome());

        VBox content = new VBox(16, titleLabel, summaryLabel, ownerLabel, backButton);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setMaxWidth(540);
        return content;
    }
}
