package edu.metrostate.brewcafe.controller;

import javafx.scene.layout.BorderPane;

// Controls the starter navigation between the home screen and each role placeholder.
public class MainMenuController {
    private final BorderPane rootLayout;

    public MainMenuController(BorderPane rootLayout) {
        this.rootLayout = rootLayout;
    }

    public void showCustomerScreen() {
        rootLayout.setCenter(RolePlaceholderFactory.createCustomerPlaceholder(rootLayout));
    }

    public void showBaristaScreen() {
        rootLayout.setCenter(RolePlaceholderFactory.createBaristaPlaceholder(rootLayout));
    }

    public void showManagerScreen() {
        rootLayout.setCenter(RolePlaceholderFactory.createManagerPlaceholder(rootLayout));
    }

    public void returnToHome() {
        rootLayout.setCenter(RolePlaceholderFactory.createHomePlaceholder(rootLayout));
    }
}
