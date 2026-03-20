package edu.metrostate.brewcafe.view;

import edu.metrostate.brewcafe.controller.RolePlaceholderFactory;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

// Builds the landing view that routes into the customer, barista, and manager placeholders.
public class MainMenuView {
    public Parent build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));
        root.setCenter(RolePlaceholderFactory.createHomePlaceholder(root));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f4ede4, #efe2d0);");
        return root;
    }
}
