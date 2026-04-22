package edu.metrostate.brewcafe.controller;

import edu.metrostate.brewcafe.service.CafeApplicationState;
import javafx.scene.layout.BorderPane;

// Controls the starter navigation between the home screen and each role placeholder.
public class MainMenuController {
    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;

    public MainMenuController(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;
    }

    public void showCustomerScreen() {
        rootLayout.setCenter(RolePlaceholderFactory.createCustomerPlaceholder(rootLayout, applicationState));
    }

    public void showBaristaScreen() {
        rootLayout.setCenter(RolePlaceholderFactory.createBaristaPlaceholder(rootLayout, applicationState));
    }

    public void showManagerScreen() {
        rootLayout.setCenter(RolePlaceholderFactory.createManagerPlaceholder(rootLayout, applicationState));
    }

    public void returnToHome() {
        rootLayout.setCenter(RolePlaceholderFactory.createHomePlaceholder(rootLayout, applicationState));
    }
}
